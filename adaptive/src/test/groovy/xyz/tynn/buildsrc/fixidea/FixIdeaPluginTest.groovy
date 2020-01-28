//  Copyright 2019 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.fixidea

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource

@DisplayName('FixIdeaPlugin')
class FixIdeaPluginTest {

    Project rootProject
    Project subProject

    @BeforeEach
    void setup() {
        rootProject = ProjectBuilder
                .builder()
                .withName('root')
                .build()
        subProject = ProjectBuilder
                .builder()
                .withName('sub')
                .withParent(rootProject)
                .build()
        rootProject.apply plugin: FixIdeaPlugin
    }

    @Test
    void 'should be lazy'() {
        rootProject.plugins.with {
            assert size() == 1
            assert hasPlugin(FixIdeaPlugin)
        }
        subProject.plugins.with {
            assert size() == 0
        }
    }

    @Nested
    @DisplayName('with kotlin-android')
    class OnApplyKotlinAndroid {

        @ParameterizedTest
        @ArgumentsSource(RootAndSubProjectsProvider)
        void 'should add main and test kotlin sources to java'(project) {
            this."$project".with {
                apply plugin: 'com.android.library'
                apply plugin: 'kotlin-android'

                android {
                    defaultConfig {
                        compileSdkVersion 29
                    }
                }

                evaluate()

                def javaSrcDirs = android.sourceSets*.java.srcDirs.flatten()

                assert new File(projectDir, 'src/main/kotlin') in javaSrcDirs
                assert new File(projectDir, 'src/test/kotlin') in javaSrcDirs
            }
        }

        @ParameterizedTest
        @ArgumentsSource(RootAndSubProjectsProvider)
        void 'should add all kotlin sources to java'(project) {
            this."$project".with {
                apply plugin: 'com.android.library'
                apply plugin: 'kotlin-android'

                android {
                    defaultConfig {
                        compileSdkVersion 29
                    }

                    buildTypes {
                        preview
                    }

                    flavorDimensions 'app', 'env'
                    productFlavors {
                        open {
                            dimension 'app'
                        }

                        closed {
                            dimension 'app'
                        }

                        prod {
                            dimension 'env'
                        }
                    }
                }

                evaluate()

                android.sourceSets.all {
                    kotlin.srcDirs.each {
                        assert it in java.srcDirs
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName('with idea')
    class OnApplyIdea {

        @ParameterizedTest
        @ArgumentsSource(RootAndSubProjectsProvider)
        void 'should be lazy'(project) {
            this."$project".with {
                apply plugin: 'java-test-fixtures'

                plugins.with {
                    assert hasPlugin('java-test-fixtures')
                    assert !hasPlugin('java')
                    assert !hasPlugin('idea')
                }
            }
        }

        @Nested
        @DisplayName('with java-test-fixtures')
        class OnApplyJavaTestFixtures {

            @ParameterizedTest
            @ArgumentsSource(RootAndSubProjectsProvider)
            void 'should be lazy'(project) {
                this."$project".with {
                    apply plugin: 'java-test-fixtures'
                    apply plugin: 'idea'

                    plugins.with {
                        assert hasPlugin('java-test-fixtures')
                        assert hasPlugin('idea')
                        assert !hasPlugin('java')
                    }
                }
            }

            @Nested
            @DisplayName('with java plugin')
            class OnApplyJava {

                @ParameterizedTest
                @ArgumentsSource(RootAndSubProjectsProvider)
                void 'should configure idea source sets'(project) {
                    this."$project".with {
                        apply plugin: 'java-test-fixtures'
                        apply plugin: 'java'
                        apply plugin: 'idea'

                        sourceSets.testFixtures.allSource.srcDirs.each {
                            assert it in idea.module.testSourceDirs
                            assert !(it in idea.module.sourceDirs)
                        }
                    }
                }

                @ParameterizedTest
                @ArgumentsSource(RootAndSubProjectsProvider)
                void 'should configure idea scopes'(project) {
                    this."$project".with {
                        apply plugin: 'java-test-fixtures'
                        apply plugin: 'java'
                        apply plugin: 'idea'

                        def api = ProjectBuilder
                                .builder()
                                .build()
                        def implementation = ProjectBuilder
                                .builder()
                                .build()

                        dependencies {
                            testFixturesApi api
                            testFixturesImplementation implementation
                        }

                        def provided = idea.module.scopes.PROVIDED.plus.collectMany {
                            it.allDependencies*.dependencyProject
                        }

                        assert api in provided
                        assert implementation in provided
                    }
                }
            }
        }
    }
}
