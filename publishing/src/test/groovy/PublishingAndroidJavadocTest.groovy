//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

import groovy.transform.PackageScope
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import java.util.zip.ZipFile

import static com.android.builder.model.AndroidProject.FD_OUTPUTS
import static kotlin.KotlinVersion.CURRENT
import static org.gradle.testkit.runner.GradleRunner.create
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading.readImplementationClasspath
import static org.junit.jupiter.params.provider.Arguments.arguments

class PublishingAndroidJavadocTest {

    static def gradleVersions = [
            '5.6.4',
            '6.0.1',
            '6.1'
    ]
    static def androidVersions = [
            '3.6.+': gradleVersions,
            //'4.0.+': gradleVersions[2..-1],
    ]

    @TempDir
    @PackageScope
    File projectDir

    File buildFile


    @BeforeEach
    void setup() {
        def classpath = readImplementationClasspath().collect {
            "classpath files('$it')"
        }
        buildFile = new File(projectDir, 'build.gradle')
        buildFile << """
            buildscript {
                ext.hasKotlin = project.findProperty('hasKotlin')
                repositories {
                    google()
                    jcenter()
                }
                dependencies {
                    ${classpath.join('\n')}
                    classpath "com.android.tools.build:gradle:\$androidVersion"
                    if (hasKotlin)
                        classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:$CURRENT'
                }
            }

            repositories {
                google()
                jcenter()
            }

            apply plugin: 'xyz.tynn.android.javadoc'
            
            plugins.withId('com.android.library') {
                if (hasKotlin) project.apply plugin: 'kotlin-android'
            }

            group = 'com.example.test'
        """

        ['main', 'debug', 'release'].each {
            def sources = new File(projectDir, "src/$it/java/com/example/test")
            sources.mkdirs()
            new File(sources, "${it.capitalize()}Example.java") << """
                package com.example.test;
                /** Some javadoc **/
                public class ${it.capitalize()}Example { }
            """
            sources = new File(projectDir, "src/$it/kotlin/com/example/test")
            sources.mkdirs()
            new File(sources, "${it.capitalize()}Example.kt") << """
                package com.example.test
                /** Some kdoc **/
                fun ${it.capitalize()}Example?.globalExtFunction() = this 
            """
        }
        new File(projectDir, 'src/main/AndroidManifest.xml') << '<manifest package="com.example.test"/>'
    }

    @ParameterizedTest(name = "Gradle={0} Android={1} Kotlin={2}")
    @MethodSource("gradleAndAndroidVersions")
    @DisplayName('build should create all Android javadoc JARs')
    void shouldBuildAndroidJavadocJars(g, a, hasKotlin) {
        buildFile << """
            apply plugin: 'com.android.library'

            android {
                compileSdkVersion 29
                buildToolsVersion '29.0.2'
            }
        """

        def result = prepareGradleRunner(g, a, hasKotlin)
                .withArguments(
                        'build',
                )
                .build()

        assert result.task(':debugJavadoc').outcome == SUCCESS
        assert result.task(':debugJavadocJar').outcome == SUCCESS
        assert result.task(':releaseJavadoc').outcome == SUCCESS
        assert result.task(':releaseJavadocJar').outcome == SUCCESS
        assert result.task(':build').outcome == SUCCESS

        assertJavadoc projectDir, 'debug'
        assertJavadoc projectDir, 'release'
    }

    @ParameterizedTest(name = "Gradle={0} Android={1} Kotlin={2}")
    @MethodSource("gradleAndAndroidVersions")
    @DisplayName('publishToMavenLocal should publish all Android javadoc JARs')
    void shouldPublishAndroidSourcesJars(g, a, hasKotlin) {
        buildFile << """
            apply plugin: 'com.android.library'
            apply plugin: 'maven-publish'            

            android {
                compileSdkVersion 29
                buildToolsVersion '29.0.2'
            }

            afterEvaluate {
                publishing {
                    publications {
                        android(MavenPublication) {
                            from components.all
                            artifactId = 'android'
                        }
                        debug(MavenPublication) {
                            from components.debug
                            artifactId = 'debug'
                        }
                        release(MavenPublication) {
                            from components.release
                            artifactId = 'release'
                        }
                    }
                }
            }
        """

        def version = "$a$g+$hasKotlin"
        def mavenLocal = new File("$projectDir/m2/repository")
        def result = prepareGradleRunner(g, a, hasKotlin)
                .withArguments(
                        "-Dmaven.repo.local=$mavenLocal",
                        'publishToMavenLocal',
                        "-Pversion=$version",
                )
                .build()

        assert result.task(':debugJavadoc').outcome == SUCCESS
        assert result.task(':debugJavadocJar').outcome == SUCCESS
        assert result.task(':releaseJavadoc').outcome == SUCCESS
        assert result.task(':releaseJavadocJar').outcome == SUCCESS
        assert result.task(':publishToMavenLocal').outcome == SUCCESS

        def mavenGroup = new File(mavenLocal, 'com/example/test')

        assertJavadoc mavenGroup, 'android', version, 'debug'
        assertJavadoc mavenGroup, 'android', version, 'release'
        assertJavadoc mavenGroup, 'debug', version
        assertJavadoc mavenGroup, 'release', version
    }

    def prepareGradleRunner(gradleVersion, androidVersion, hasKotlin) {
        new File(projectDir, 'gradle.properties') << """
            androidVersion=$androidVersion
            ${hasKotlin ? 'hasKotlin' : 'javaOnly'}=true
        """
        return create()
                .withProjectDir(projectDir)
                .withGradleVersion(gradleVersion)
    }

    static assertJavadoc(project, variant) {
        def name = "$project.name-${variant}.jar"
        assertJavadoc new File(project, "build/$FD_OUTPUTS/javadoc/$name")
    }

    static assertJavadoc(group, artifactId, version, variant = null) {
        def name = "$artifactId-$version-${variant ? "$variant-javadoc" : 'javadoc'}.jar"
        assertJavadoc new File(group, "$artifactId/$version/$name")
    }

    static assertJavadoc(artifact) {
        assert artifact.exists()
        new ZipFile(artifact).with {
            assert entries().findAll {
                it.name =~ /^index(-all)?.html$/
            }.size() == 2
            def files = entries().findAll {
                it.name =~ /(Main|Debug|Release)Example.html$/
            }
            assert files.size() == 2
            assert files.every {
                getInputStream(it).filterLine {
                    it.contains('https://developer.android.com/reference/')
                }.toString()
            }
        }
    }

    static gradleAndAndroidVersions() {
        androidVersions.collectMany { a, gradleVersions ->
            gradleVersions.collectMany { g ->
                [
                        arguments(g, a, true),
                        arguments(g, a, false),
                ]
            }
        }
    }
}
