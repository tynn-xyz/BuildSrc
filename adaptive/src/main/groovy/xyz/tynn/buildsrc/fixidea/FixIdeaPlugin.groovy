//  Copyright 2019 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.fixidea

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Fix minor issues with IDEA whenever these popup.
 */
final class FixIdeaPlugin implements Plugin<Project> {

    @Override
    void apply(Project rootProject) {
        rootProject.allprojects {

            // setup kotlin sources for Android Studio
            plugins.withId('kotlin-android') {
                afterEvaluate {
                    android {
                        sourceSets.all {
                            java.srcDirs += kotlin.srcDirs
                        }
                    }
                }
            }

            // add test fixture sources and dependencies for IDEA
            plugins.withId('java-test-fixtures') {
                plugins.withId('java') {
                    project.apply plugin: 'idea'

                    configurations {
                        ideaTestFixturesClasspath {
                            extendsFrom testFixturesApi
                            extendsFrom testFixturesImplementation
                            canBeConsumed false
                            canBeResolved true
                        }
                    }

                    idea {
                        module {
                            sourceDirs -= sourceSets.testFixtures.allSource
                            testSourceDirs += sourceSets.testFixtures.allSource.srcDirs
                            scopes.PROVIDED.plus += [configurations.ideaTestFixturesClasspath]
                        }
                    }
                }
            }
        }
    }
}
