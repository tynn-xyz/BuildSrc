//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing

import groovy.transform.PackageScope
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import java.util.zip.ZipFile

import static org.gradle.testkit.runner.GradleRunner.create
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading.readImplementationClasspath

@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class AndroidJavadocPluginFuncTest {

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
                ext.kotlinVersion = project.findProperty('kotlinVersion')
                repositories {
                    google()
                    mavenCentral()
                }
                dependencies {
                    ${classpath.join('\n')}
                    classpath "com.android.tools.build:gradle:\$androidVersion"
                    if (kotlinVersion)
                        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:\$kotlinVersion"
                }
            }

            repositories {
                google()
                mavenCentral()
            }

            apply plugin: 'xyz.tynn.android.javadoc'
            
            plugins.withId('com.android.library') {
                if (kotlinVersion)
                    project.apply plugin: 'kotlin-android'
            }

            group = 'com.example.test'
        """

        ['main', 'debug', 'release'].each {
            def sources = new File(projectDir, "src/$it/java/com/example/test")
            sources.mkdirs()
            new File(sources, "${it.capitalize()}Example.java") << """
                package com.example.test;
                /** Some Javadoc **/
                public class ${it.capitalize()}Example { }
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

    @ParameterizedTest(name = "Android={0} Gradle={1} Kotlin={2}")
    @MethodSource("xyz.tynn.buildsrc.publishing.AndroidGradleVersions#provide")
    @DisplayName('build should create all Android javadoc JARs')
    void shouldBuildAndroidJavadocJars(a, g, k) {
        buildFile << """
            apply plugin: 'com.android.library'

            android {
                compileSdkVersion 31
            }
        """

        def result = prepareGradleRunner(a, g, k)
                .withArguments(
                        'build',
                        '-xlint',
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

    @ParameterizedTest(name = "Android={0} Gradle={1} Kotlin={2}")
    @MethodSource("xyz.tynn.buildsrc.publishing.AndroidGradleVersions#provide")
    @DisplayName('publishToMavenLocal should publish all Android javadoc JARs')
    void shouldPublishAndroidJavadocJars(a, g, k) {
        buildFile << """
            apply plugin: 'com.android.library'
            apply plugin: 'maven-publish'

            android {
                compileSdkVersion 31
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

        def version = "$a$g+$k"
        def mavenLocal = new File("$projectDir/m2/repository")
        def result = prepareGradleRunner(a, g, k)
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

    def prepareGradleRunner(androidVersion, gradleVersion, kotlinVersion) {
        new File(projectDir, 'gradle.properties') << """
            androidVersion=$androidVersion
            ${kotlinVersion ? "kotlinVersion=$kotlinVersion" : 'javaOnly=true'}
        """
        return create()
                .withProjectDir(projectDir)
                .withGradleVersion(gradleVersion)
    }

    static assertJavadoc(project, variant) {
        def name = "$project.name-${variant}.jar"
        assertJavadoc new File(project, "build/outputs/javadoc/$name")
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
}
