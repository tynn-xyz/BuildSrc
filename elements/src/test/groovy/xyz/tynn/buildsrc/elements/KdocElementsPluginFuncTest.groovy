//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.elements

import groovy.transform.PackageScope
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import java.util.zip.ZipFile

import static kotlin.KotlinVersion.CURRENT
import static org.gradle.testkit.runner.GradleRunner.create
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading.readImplementationClasspath
import static org.junit.jupiter.params.provider.Arguments.arguments

class KdocElementsPluginFuncTest {

    static def gradleVersions = [
//            '6.0.1',
//            '6.1.1',
            '6.2.2',
            '6.3',
            '6.4',
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
                    jcenter()
                }
                dependencies {
                    ${classpath.join('\n')}
                    classpath 'org.jetbrains.dokka:dokka-gradle-plugin:0.10.1'
                    classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:$CURRENT'
                }
            }

            repositories {
                jcenter()
            }

            apply plugin: 'xyz.tynn.jvm.kdoc'

            group = 'com.example.test'
        """

        ['main'].each {
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
    }

    @ParameterizedTest(name = "Gradle={0}")
    @MethodSource("gradleVersions")
    @DisplayName('build should create Java KDoc JAR')
    void shouldBuildJavaKdocJars(g) {
        buildFile << """
            apply plugin: 'kotlin'

            java {
                withKdocJar()
            }
        """

        def result = prepareGradleRunner(g)
                .withArguments(
                        'build',
                )
                .build()

        assert result.task(':dokka').outcome == SUCCESS
        assert result.task(':kdocJar').outcome == SUCCESS
        assert result.task(':build').outcome == SUCCESS

        assertKdoc new File(projectDir, "build/libs/$projectDir.name-kdoc.jar")
    }

    @ParameterizedTest(name = "Gradle={0}")
    @MethodSource("gradleVersions")
    @DisplayName('publishToMavenLocal should publish Java KDoc JAR')
    void shouldPublishJavaKdocJars(g) {
        buildFile << """
            apply plugin: 'kotlin'
            apply plugin: 'maven-publish'

            java {
                withKdocJar()
            }

            publishing {
                publications {
                    java(MavenPublication) {
                        from components.java
                        artifactId = 'java'
                    }
                }
            }
        """

        def mavenLocal = new File("$projectDir/m2/repository")
        def result = prepareGradleRunner(g)
                .withArguments(
                        "-Dmaven.repo.local=$mavenLocal",
                        'publishToMavenLocal',
                        "-Pversion=$g",
                )
                .build()

        assert result.task(':dokka').outcome == SUCCESS
        assert result.task(':kdocJar').outcome == SUCCESS
        assert result.task(':publishToMavenLocal').outcome == SUCCESS

        def mavenGroup = new File(mavenLocal, 'com/example/test')

        assertKdoc mavenGroup, 'java', g
    }

    def prepareGradleRunner(gradleVersion) {
        return create()
                .withProjectDir(projectDir)
                .withGradleVersion(gradleVersion)
    }

    static assertKdoc(group, artifactId, version) {
        def name = "$artifactId-$version-kdoc.jar"
        assertKdoc new File(group, "$artifactId/$version/$name")
    }

    static assertKdoc(File artifact) {
        assert artifact.exists()
        new ZipFile(artifact).with {
            assert entries().findAll {
                it.name =~ /^[^\/]+\/index(-outline)?.html$/
            }.size() == 2
            assert entries().find {
                it.name =~ /global-ext-function.html$/
            }
            assert entries().find {
                it.name =~ /-main-example\/index.html$/
            }
        }
    }

    static gradleVersions() {
        gradleVersions.collect {
            arguments it
        }
    }
}
