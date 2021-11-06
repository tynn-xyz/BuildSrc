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
class AndroidKdocPluginFuncTest {

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
                repositories {
                    google()
                    mavenCentral()
                    gradlePluginPortal()
                }
                dependencies {
                    ${classpath.join('\n')}
                    classpath "com.android.tools.build:gradle:\$androidVersion"
                    classpath "org.jetbrains.dokka:dokka-gradle-plugin:\$kotlinVersion"
                    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:\$kotlinVersion"
                }
            }

            repositories {
                google()
                mavenCentral()
                gradlePluginPortal()
            }

            apply plugin: 'xyz.tynn.android.kdoc'
            
            plugins.withId('com.android.library') {
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

    @ParameterizedTest(name = "Android={0} Gradle={1} Kotlin={2}")
    @MethodSource("xyz.tynn.buildsrc.publishing.AndroidGradleVersions#provideKotlinOnly")
    @DisplayName('build should create all Android KDoc JARs')
    void shouldBuildAndroidKdocJars(a, g, k) {
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

        assert result.task(':debugKdoc').outcome == SUCCESS
        assert result.task(':debugKdocJar').outcome == SUCCESS
        assert result.task(':releaseKdoc').outcome == SUCCESS
        assert result.task(':releaseKdocJar').outcome == SUCCESS
        assert result.task(':build').outcome == SUCCESS

        assertKdoc projectDir, 'debug'
        assertKdoc projectDir, 'release'
    }

    @ParameterizedTest(name = "Android={0} Gradle={1} Kotlin={2}")
    @MethodSource("xyz.tynn.buildsrc.publishing.AndroidGradleVersions#provideKotlinOnly")
    @DisplayName('publishToMavenLocal should publish all Android KDoc JARs')
    void shouldPublishAndroidKdocJars(a, g, k) {
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

        def version = "$a$g+true"
        def mavenLocal = new File("$projectDir/m2/repository")
        def result = prepareGradleRunner(a, g, k)
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

    def prepareGradleRunner(androidVersion, gradleVersion, kotlinVersion) {
        new File(projectDir, 'gradle.properties') << """
            androidVersion=$androidVersion
            kotlinVersion=$kotlinVersion
        """
        return create()
                .withProjectDir(projectDir)
                .withGradleVersion(gradleVersion)
    }

    static assertKdoc(project, variant) {
        def name = "$project.name-${variant}.jar"
        assertKdoc new File(project, "build/outputs/kdoc/$name")
    }

    static assertKdoc(group, artifactId, version, variant = null) {
        def name = "$artifactId-$version-${variant ? "$variant-kdoc" : 'kdoc'}.jar"
        assertKdoc new File(group, "$artifactId/$version/$name")
    }

    static assertKdoc(artifact) {
        assert artifact.exists()
        new ZipFile(artifact).with {
            entries().each {
                println it
            }
            assert entries().find {
                it.name =~ /^[^\/]+\/com.example.test\/index.html$/
            }
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
}
