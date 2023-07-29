//  Copyright 2023 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

import org.gradle.api.Project
import org.gradle.api.tasks.Delete
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import static org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import static org.gradle.api.tasks.wrapper.Wrapper.DistributionType.ALL

@DisplayName('xyz.tynn.convention.project')
class XyzTynnConventionProjectPluginTest {

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
        rootProject.apply plugin: XyzTynnConventionProjectPlugin
    }

    @Test
    void 'should be lazy'() {
        rootProject.plugins.with {
            assert size() == 1
            assert hasPlugin(XyzTynnConventionProjectPlugin)
        }

        assert subProject.plugins.size() == 0
    }

    @Test
    void 'should register clean task on root after evaluate'() {
        rootProject.with {
            assert !tasks.findByName('clean')
            evaluate()
            assert tasks.getByName('clean') instanceof Delete
        }
    }

    @Test
    void 'should not register duplicate clean task'() {
        rootProject.with {
            task 'clean'
            evaluate()
            assert tasks.getByName('clean') !instanceof Delete
        }
    }

    @Test
    void 'should not create clean task on subproject'() {
        subProject.with {
            evaluate()
            assert !tasks.findByName('clean')
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ['rootProject', 'subProject'])
    void 'should configure test logging'(project) {
        this."$project".with {
            task 'test', type: org.gradle.api.tasks.testing.Test
            tasks.test.testLogging {
                assert events == [FAILED] as Set
                assert exceptionFormat == FULL
                assert showCauses
                assert showExceptions
                assert showStackTraces
            }
        }
    }

    @Nested
    @DisplayName('with wrapper')
    class OnWrapper {

        @Test
        void 'should configure distributionType ALL'() {
            rootProject.with {
                apply plugin: 'wrapper'
                assert wrapper.distributionType == ALL
            }

            assert rootProject.plugins.size() == 2
            assert subProject.plugins.size() == 0
        }

        @Test
        void 'should not configure subproject'() {
            subProject.with {
                apply plugin: XyzTynnConventionProjectPlugin
                apply plugin: 'wrapper'
            }

            assert rootProject.plugins.size() == 1
            assert subProject.plugins.size() == 2
        }
    }

    @Nested
    @DisplayName('with maven-publish')
    class OnMavenPublish {

        @ParameterizedTest
        @ValueSource(strings = ['rootProject', 'subProject'])
        void 'should apply XyzTynnConventionReleasePlugin'(project) {
            this."$project".with {
                apply plugin: 'maven-publish'
                assert plugins.hasPlugin(XyzTynnConventionReleasePlugin)
            }
        }
    }

    @Nested
    @DisplayName('with com.android.application')
    class OnAndroidApplication {

        @ParameterizedTest
        @ValueSource(strings = ['rootProject', 'subProject'])
        void 'should apply XyzTynnConventionAndroidPlugin'(project) {
            this."$project".with {
                apply plugin: 'com.android.application'
                assert plugins.hasPlugin(XyzTynnConventionAndroidPlugin)
            }
        }
    }

    @Nested
    @DisplayName('with com.android.library')
    class OnAndroidLibrary {

        @ParameterizedTest
        @ValueSource(strings = ['rootProject', 'subProject'])
        void 'should apply XyzTynnConventionAndroidPlugin'(project) {
            this."$project".with {
                apply plugin: 'com.android.library'
                assert plugins.hasPlugin(XyzTynnConventionAndroidPlugin)
            }
        }
    }

    @Nested
    @DisplayName('with org.jetbrains.kotlin.android')
    class OnKotlinAndroid {

        @ParameterizedTest
        @ValueSource(strings = ['rootProject', 'subProject'])
        void 'should apply XyzTynnConventionKotlinPlugin'(project) {
            this."$project".with {
                apply plugin: 'com.android.library'
                apply plugin: 'org.jetbrains.kotlin.android'
                assert plugins.hasPlugin(XyzTynnConventionKotlinPlugin)
            }
        }
    }

    @Nested
    @DisplayName('with org.jetbrains.kotlin.jvm')
    class OnKotlinJvm {

        @ParameterizedTest
        @ValueSource(strings = ['rootProject', 'subProject'])
        void 'should apply XyzTynnConventionKotlinPlugin'(project) {
            this."$project".with {
                apply plugin: 'org.jetbrains.kotlin.jvm'
                assert plugins.hasPlugin(XyzTynnConventionKotlinPlugin)
            }
        }
    }
}
