//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing

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

class AndroidKdocPluginFuncTest {

    static def gradleVersions = [
            '5.6.4',
            '6.0.1',
            '6.1.1',
            '6.2',
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
                    classpath 'org.jetbrains.dokka:dokka-gradle-plugin:0.10.1'
                    if (hasKotlin)
                        classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:$CURRENT'
                }
            }

            repositories {
                google()
                jcenter()
            }

            apply plugin: 'xyz.tynn.android.kdoc'
            
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
                /** Some Javadoc **/
                public class ${it.capitalize()}Example extends android.app.Application { }
            """
            sources = new File(projectDir, "src/$it/kotlin/com/example/test")
            sources.mkdirs()
            new File(sources, "${it.capitalize()}Example.kt") << """
                package com.example.test
                /** Some KDoc **/
                fun ${it.capitalize()}Example?.globalExtFunction() = this 
            """
        }
        new File(projectDir, 'src/main/AndroidManifest.xml') << '<manifest package="com.example.test"/>'
    }

    @ParameterizedTest(name = "Gradle={0} Android={1} Kotlin={2}")
    @MethodSource("gradleAndAndroidVersions")
    @DisplayName('build should create all Android KDoc JARs')
    void shouldBuildAndroidKdocJars(g, a, hasKotlin) {
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
                        '-xlint',
                )
                .build()

        assert result.task(':debugKdoc').outcome == SUCCESS
        assert result.task(':debugKdocJar').outcome == SUCCESS
        assert result.task(':releaseKdoc').outcome == SUCCESS
        assert result.task(':releaseKdocJar').outcome == SUCCESS
        assert result.task(':build').outcome == SUCCESS

        assertKdoc projectDir, 'debug'
        assertKdoc projectDir, 'release'
    }

    @ParameterizedTest(name = "Gradle={0} Android={1} Kotlin={2}")
    @MethodSource("gradleAndAndroidVersions")
    @DisplayName('publishToMavenLocal should publish all Android KDoc JARs')
    void shouldPublishAndroidKdocJars(g, a, hasKotlin) {
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

        assert result.task(':debugKdoc').outcome == SUCCESS
        assert result.task(':debugKdocJar').outcome == SUCCESS
        assert result.task(':releaseKdoc').outcome == SUCCESS
        assert result.task(':releaseKdocJar').outcome == SUCCESS
        assert result.task(':publishToMavenLocal').outcome == SUCCESS

        def mavenGroup = new File(mavenLocal, 'com/example/test')

        assertKdoc mavenGroup, 'android', version, 'debug'
        assertKdoc mavenGroup, 'android', version, 'release'
        assertKdoc mavenGroup, 'debug', version
        assertKdoc mavenGroup, 'release', version
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

    static assertKdoc(project, variant) {
        def name = "$project.name-${variant}.jar"
        assertKdoc new File(project, "build/$FD_OUTPUTS/kdoc/$name")
    }

    static assertKdoc(group, artifactId, version, variant = null) {
        def name = "$artifactId-$version-${variant ? "$variant-kdoc" : 'kdoc'}.jar"
        assertKdoc new File(group, "$artifactId/$version/$name")
    }

    static assertKdoc(artifact) {
        assert artifact.exists()
        new ZipFile(artifact).with {
            assert entries().findAll {
                it.name =~ /^[^\/]+\/index(-outline)?.html$/
            }.size() == 2
            assert entries().find {
                it.name =~ /global-ext-function.html$/
            }
            def files = entries().findAll {
                it.name =~ /-(main|debug|release)-example\/index.html$/
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
            gradleVersions.collect { g ->
                arguments g, a, true
            }
        }
    }
}
