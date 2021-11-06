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
class AndroidSourcesPluginFuncTest {

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

            apply plugin: 'xyz.tynn.android.sources'
            
            plugins.withId('com.android.library') {
                if (kotlinVersion)
                    project.apply plugin: 'kotlin-android'
            }

            group = 'com.example.test'
        """

        ['main', 'debug', 'release'].each {
            def sources = new File(projectDir, "src/$it/java/com/example/test")
            sources.mkdirs()
            new File(sources, "${it.capitalize()}Example.java") << "package com.example.test;"
            sources = new File(projectDir, "src/$it/kotlin/com/example/test")
            sources.mkdirs()
            new File(sources, "${it.capitalize()}Example.kt") << "package com.example.test"
        }
        new File(projectDir, 'src/main/AndroidManifest.xml') << '<manifest package="com.example.test"/>'
    }

    @ParameterizedTest(name = "Android={0} Gradle={1} Kotlin={2}")
    @MethodSource("xyz.tynn.buildsrc.publishing.AndroidGradleVersions#provide")
    @DisplayName('build should create all Android sources JARs')
    void shouldBuildAndroidSourcesJars(a, g, k) {
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

        assert result.task(':debugSourcesJar').outcome == SUCCESS
        assert result.task(':releaseSourcesJar').outcome == SUCCESS
        assert result.task(':build').outcome == SUCCESS

        assertSources !k, projectDir, 'debug'
        assertSources !k, projectDir, 'release'
    }

    @ParameterizedTest(name = "Android={0} Gradle={1} Kotlin={2}")
    @MethodSource("xyz.tynn.buildsrc.publishing.AndroidGradleVersions#provide")
    @DisplayName('publishToMavenLocal should publish all Android sources JARs')
    void shouldPublishAndroidSourcesJars(a, g, k) {
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

        assert result.task(':debugSourcesJar').outcome == SUCCESS
        assert result.task(':releaseSourcesJar').outcome == SUCCESS
        assert result.task(':publishToMavenLocal').outcome == SUCCESS

        def mavenGroup = new File(mavenLocal, 'com/example/test')

        assertSources !k, mavenGroup, 'android', version, 'debug'
        assertSources !k, mavenGroup, 'android', version, 'release'
        assertSources !k, mavenGroup, 'debug', version
        assertSources !k, mavenGroup, 'release', version
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

    static assertSources(javaOnly, project, variant) {
        def name = "$project.name-${variant}.jar"
        assertSources javaOnly, new File(project, "build/outputs/sources/$name")
    }

    static assertSources(javaOnly, group, artifactId, version, variant = null) {
        def name = "$artifactId-$version-${variant ? "$variant-sources" : 'sources'}.jar"
        assertSources javaOnly, new File(group, "$artifactId/$version/$name")
    }

    static assertSources(javaOnly, artifact) {
        assert artifact.exists()
        new ZipFile(artifact).with {
            assert entries().findAll {
                it.name =~ /(Main|Debug|Release)Example.java$/
            }.size() == 2
            assert javaOnly || entries().findAll {
                it.name =~ /(Main|Debug|Release)Example.kt$/
            }.size() == 2
        }
    }
}
