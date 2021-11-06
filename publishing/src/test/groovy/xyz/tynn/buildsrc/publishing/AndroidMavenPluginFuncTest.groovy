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

import static org.gradle.testkit.runner.GradleRunner.create
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading.readImplementationClasspath
import static org.gradle.util.GradleVersion.version as gradleVersion

@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class AndroidMavenPluginFuncTest {

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

            apply plugin: 'xyz.tynn.android.maven'
            
            plugins.withId('com.android.library') {
                if (kotlinVersion)
                    project.apply plugin: 'kotlin-android'
            }

            group = 'com.example.test'
        """

        new File(projectDir, 'src/main/').mkdirs()
        new File(projectDir, 'src/main/AndroidManifest.xml') << '<manifest package="com.example.test"/>'
    }

    @ParameterizedTest(name = "Android={0} Gradle={1} Kotlin={2}")
    @MethodSource("xyz.tynn.buildsrc.publishing.AndroidGradleVersions#provide")
    @DisplayName('publishToMavenLocal should publish all Android release components')
    void shouldPublishAndroidReleaseComponents(a, g, k) {
        buildFile << """
            apply plugin: 'com.android.library'

            android {
                compileSdkVersion 31

                flavorDimensions 'scope', 'context'
                productFlavors {
                    open {
                        dimension 'scope'
                    }

                    closed {
                        dimension 'scope'
                    }

                    binary {
                        dimension 'context'
                    }

                    source {
                        dimension 'context'
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

        assert result.task(':publishClosedBinaryReleasePublicationToMavenLocal').outcome == SUCCESS
        assert result.task(':publishClosedSourceReleasePublicationToMavenLocal').outcome == SUCCESS
        assert result.task(':publishOpenBinaryReleasePublicationToMavenLocal').outcome == SUCCESS
        assert result.task(':publishOpenSourceReleasePublicationToMavenLocal').outcome == SUCCESS
        assert result.task(':publishToMavenLocal').outcome == SUCCESS

        def mavenGroup = new File(mavenLocal, 'com/example/test')

        assertMavenPublication mavenGroup, projectDir, version, 'closed-binary'
        assertMavenPublication mavenGroup, projectDir, version, 'closed-source'
        assertMavenPublication mavenGroup, projectDir, version, 'open-binary'
        assertMavenPublication mavenGroup, projectDir, version, 'open-source'

        if (gradleVersion(g.toString()) >= gradleVersion('6.0')) {
            assert result.task(':publishReleasePublicationToMavenLocal').outcome == SUCCESS
            assertMavenPublication mavenGroup, projectDir, version
        }
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

    static assertMavenPublication(mavenGroup, project, version, variant) {
        def dir = new File(mavenGroup, "$project.name-$variant/$version")
        def baseName = "$project.name-$variant-$version"
        assert new File(dir, "${baseName}.aar").exists()
        assert new File(dir, "${baseName}.pom").exists()
    }

    static assertMavenPublication(mavenGroup, project, version) {
        def dir = new File(mavenGroup, "$project.name/$version")
        def baseName = "$project.name-$version"
        assert new File(dir, "${baseName}.module").exists()
        assert new File(dir, "${baseName}.pom").exists()
    }
}
