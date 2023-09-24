//  Copyright 2023 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.internal.plugins.PluginApplicationException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static org.junit.jupiter.api.Assertions.assertInstanceOf
import static org.junit.jupiter.api.Assertions.assertThrows

@DisplayName('xyz.tynn.convention.android')
class XyzTynnConventionAndroidPluginTest {
    final String name = 'android'
    final String group = 'xyz.tynn.test'

    Project project

    @BeforeEach
    void setup() {
        project = ProjectBuilder
                .builder()
                .withName(name)
                .build()
        project.group = group
    }

    @Test
    void 'should not be lazy'() {
        assertThrows(PluginApplicationException) {
            project.apply plugin: XyzTynnConventionAndroidPlugin
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ['application', 'library'])
    void 'should set default configurations'(plugin) {
        project.with {
            apply plugin: "com.android.$plugin"
            apply plugin: XyzTynnConventionAndroidPlugin
            android {
                assert namespace == "${group}.${name}"
                assert compileSdk == 34
                defaultConfig {
                    assert minSdk == 24
                    assert targetSdk == 34
                    assert testInstrumentationRunner == 'androidx.test.runner.AndroidJUnitRunner'
                }
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ['application', 'library'])
    void 'should configure lint'(plugin) {
        project.with {
            apply plugin: "com.android.$plugin"
            apply plugin: XyzTynnConventionAndroidPlugin
            android.lint {
                assert abortOnError
                assert warningsAsErrors
            }
        }
    }

    @Nested
    @DisplayName('with org.jetbrains.kotlin.android')
    class OnKotlin {

        @ParameterizedTest
        @ValueSource(strings = ['application', 'library'])
        void 'should add -Xexplicit-api=strict after evaluate'(plugin) {
            project.with {
                apply plugin: "com.android.$plugin"
                apply plugin: 'org.jetbrains.kotlin.android'
                apply plugin: XyzTynnConventionAndroidPlugin
                kotlin.explicitApi()
                android.kotlinOptions {
                    assert freeCompilerArgs == []
                    evaluate()
                    assert freeCompilerArgs == ['-Xexplicit-api=strict']
                }
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ['application', 'library'])
        void 'should add -Xexplicit-api=warning after evaluate'(plugin) {
            project.with {
                apply plugin: "com.android.$plugin"
                apply plugin: 'org.jetbrains.kotlin.android'
                apply plugin: XyzTynnConventionAndroidPlugin
                kotlin.explicitApiWarning()
                android.kotlinOptions {
                    assert freeCompilerArgs == []
                    evaluate()
                    assert freeCompilerArgs == ['-Xexplicit-api=warning']
                }
            }
        }
    }

    @Nested
    @DisplayName('with android.defaults.sdk.min')
    class WithMinSdkProperty {

        final propertyName = 'android.defaults.sdk.min'

        @BeforeEach
        void setup() {
            project.ext."$propertyName" = null
        }

        @ParameterizedTest
        @ValueSource(strings = ['application', 'library'])
        void 'should set minSdk from int'(plugin) {
            project.with {
                apply plugin: "com.android.$plugin"
                setProperty propertyName, 21
                apply plugin: XyzTynnConventionAndroidPlugin
                assert android.defaultConfig.minSdk == 21
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ['application', 'library'])
        void 'should set minSdk from string'(plugin) {
            project.with {
                apply plugin: "com.android.$plugin"
                setProperty propertyName, '19'
                apply plugin: XyzTynnConventionAndroidPlugin
                assert android.defaultConfig.minSdk == 19
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ['application', 'library'])
        void 'should set minSdk to default on null'(plugin) {
            project.with {
                apply plugin: "com.android.$plugin"
                apply plugin: XyzTynnConventionAndroidPlugin
                assert android.defaultConfig.minSdk == 24
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ['application', 'library'])
        void 'should set minSdk to default on NumberFormatException'(plugin) {
            project.with {
                apply plugin: "com.android.$plugin"
                setProperty propertyName, '19s'
                apply plugin: XyzTynnConventionAndroidPlugin
                assert android.defaultConfig.minSdk == 24
            }
        }

        @ParameterizedTest
        @ValueSource(strings = ['application', 'library'])
        void 'should throw InvalidUserDataException on non int'(plugin) {
            assertInstanceOf(
                    InvalidUserDataException,
                    assertThrows(PluginApplicationException) {
                        project.with {
                            apply plugin: "com.android.$plugin"
                            setProperty propertyName, project
                            apply plugin: XyzTynnConventionAndroidPlugin
                        }
                    }.cause.cause,
            )
        }
    }

    @Test
    void 'should configure build types for application'() {
        project.with {
            apply plugin: 'com.android.application'
            apply plugin: XyzTynnConventionAndroidPlugin
            android.buildTypes {
                debug {
                    assert applicationIdSuffix == '.debug'
                    assert versionNameSuffix == '+debug'
                }
                release {
                    assert minifyEnabled
                    assert proguardFiles == [
                            android.getDefaultProguardFile('proguard-android-optimize.txt'),
                    ]
                    assert signingConfig == android.signingConfigs.debug
                }
            }
        }
    }
}
