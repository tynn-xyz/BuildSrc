//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing

import groovy.transform.PackageScope
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import static kotlin.KotlinVersion.CURRENT
import static org.gradle.testkit.runner.GradleRunner.create
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading.readImplementationClasspath
import static org.gradle.util.GradleVersion.version as gradleVersion
import static org.junit.jupiter.params.provider.Arguments.arguments

class AndroidMavenPluginFuncTest {

    static def gradleVersions = [
//            '5.6.4',
//            '6.0.1',
//            '6.1.1',
            '6.2.2',
            '6.3',
            '6.4',
    ]
    static def androidVersions = [
            '3.6.+': gradleVersions,
            '4.0.+': gradleVersions,
            '4.1.+': gradleVersions[2..-1],
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

            apply plugin: 'xyz.tynn.android.maven'
            
            plugins.withId('com.android.library') {
                if (hasKotlin) project.apply plugin: 'kotlin-android'
            }

            group = 'com.example.test'
        """

        new File(projectDir, 'src/main/').mkdirs()
        new File(projectDir, 'src/main/AndroidManifest.xml') << '<manifest package="com.example.test"/>'
    }

    @ParameterizedTest(name = "Gradle={0} Android={1} Kotlin={2}")
    @MethodSource("gradleAndAndroidVersions")
    @DisplayName('publishToMavenLocal should publish all Android release components')
    void shouldPublishAndroidReleaseComponents(g, a, hasKotlin) {
        buildFile << """
            apply plugin: 'com.android.library'

            android {
                compileSdkVersion 29
                buildToolsVersion '29.0.2'

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

        def version = "$a$g+$hasKotlin"
        def mavenLocal = new File("$projectDir/m2/repository")
        def result = prepareGradleRunner(g, a, hasKotlin)
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

    def prepareGradleRunner(gradleVersion, androidVersion, hasKotlin) {
        new File(projectDir, 'gradle.properties') << """
            androidVersion=$androidVersion
            ${hasKotlin ? 'hasKotlin' : 'javaOnly'}=true
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

    static gradleAndAndroidVersions() {
        androidVersions.collectMany { a, gradleVersions ->
            gradleVersions.collect { g ->
                arguments g, a, true
            }
        }
    }
}
