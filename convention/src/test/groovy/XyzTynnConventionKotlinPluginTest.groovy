//  Copyright 2023 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName('xyz.tynn.convention.bom')
class XyzTynnConventionKotlinPluginTest {

    Project project

    @BeforeEach
    void setup() {
        project = ProjectBuilder
                .builder()
                .build()
        project.apply plugin: 'org.jetbrains.kotlin.jvm'
        project.apply plugin: XyzTynnConventionKotlinPlugin
    }

    @Test
    void 'should add kotlin stdlib dependency'() {
        project.configurations.implementation.dependencies.with {
            assert size() == 1
            first().with {
                assert name == 'kotlin-stdlib'
                assert group == 'org.jetbrains.kotlin'
                assert version == null
            }
        }
    }

    @Test
    void 'should add kotlin test dependency'() {
        project.configurations.testImplementation.dependencies.with {
            assert size() == 2
            last().with {
                assert name == 'kotlin-test'
                assert group == 'org.jetbrains.kotlin'
                assert version == null
            }
        }
    }
}
