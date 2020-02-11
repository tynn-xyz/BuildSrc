//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

import groovy.transform.PackageScope
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

import static kotlin.KotlinVersion.CURRENT
import static org.gradle.testkit.runner.GradleRunner.create
import static org.gradle.testkit.runner.TaskOutcome.NO_SOURCE
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading.readImplementationClasspath
import static org.junit.jupiter.params.provider.Arguments.arguments

class PublishingAndroidMavenTest {

    static def gradleVersions = [
            '5.6.4',
            '6.0.1',
            '6.1.1'
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

                flavorDimensions 'app'
                productFlavors {
                    open {
                        dimension 'app'
                    }

                    closed {
                        dimension 'app'
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

        assert result.task(':publishOpenReleasePublicationToMavenLocal').outcome == SUCCESS
        assert result.task(':publishClosedReleasePublicationToMavenLocal').outcome == SUCCESS
        assert result.task(':publishToMavenLocal').outcome == SUCCESS

        def mavenGroup = new File(mavenLocal, 'com/example/test')

        println new File(mavenGroup, "$projectDir.name-closed/$version").listFiles()
        println new File(mavenGroup, "$projectDir.name-open/$version").listFiles()

        assertMavenPublication mavenGroup, projectDir, version, 'closed'
        assertMavenPublication mavenGroup, projectDir, version, 'open'
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

    static gradleAndAndroidVersions() {
        androidVersions.collectMany { a, gradleVersions ->
            gradleVersions.collect { g ->
                arguments g, a, true
            }
        }
    }
}
