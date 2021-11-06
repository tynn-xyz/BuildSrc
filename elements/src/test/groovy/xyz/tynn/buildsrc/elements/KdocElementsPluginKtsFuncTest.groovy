//  Copyright 2021 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.elements

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading.readImplementationClasspath

@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class KdocElementsPluginKtsFuncTest extends KdocElementsPluginFuncTestBase {

    @BeforeEach
    void setup() {
        def classpath = readImplementationClasspath().collect {
            """classpath(files("$it"))"""
        }
        buildFile = new File(projectDir, 'build.gradle.kts')
        buildFile << """
            import xyz.tynn.buildsrc.elements.withKdocJar

            buildscript {
                repositories {
                    gradlePluginPortal()
                }
                dependencies {
                    ${classpath.join('\n')}
                    classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.5.31")
                    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
                }
            }

            repositories {
                mavenCentral()
            }

            apply(plugin = "xyz.tynn.jvm.kdoc")

            group = "com.example.test"
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
            apply(plugin = "kotlin")

            configure<JavaPluginExtension> {
                withKdocJar()
            }
        """

        def result = prepareGradleRunner(g)
                .withArguments(
                        'build',
                )
                .build()

        assert result.task(':dokkaHtml').outcome == SUCCESS
        assert result.task(':kdocJar').outcome == SUCCESS
        assert result.task(':build').outcome == SUCCESS

        assertKdoc new File(projectDir, "build/libs/$projectDir.name-kdoc.jar")
    }

    @ParameterizedTest(name = "Gradle={0}")
    @MethodSource("gradleVersions")
    @DisplayName('publishToMavenLocal should publish Java KDoc JAR')
    void shouldPublishJavaKdocJars(g) {
        buildFile << """
            apply(plugin = "kotlin")
            apply(plugin = "maven-publish")

            configure<JavaPluginExtension> {
                withKdocJar()
            }

            configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("java") {
                        from(components["java"])
                        artifactId = "java"
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

        assert result.task(':dokkaHtml').outcome == SUCCESS
        assert result.task(':kdocJar').outcome == SUCCESS
        assert result.task(':publishToMavenLocal').outcome == SUCCESS

        def mavenGroup = new File(mavenLocal, 'com/example/test')

        assertKdoc mavenGroup, 'java', g
    }
}
