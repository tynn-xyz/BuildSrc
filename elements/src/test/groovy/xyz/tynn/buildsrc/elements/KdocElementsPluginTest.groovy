//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.elements

import org.gradle.api.Project
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.gradle.testfixtures.ProjectBuilder.builder
import static org.junit.jupiter.api.Assertions.*

class KdocElementsPluginTest {

    Project project

    @BeforeEach
    void setup() {
        project = builder().build()
    }

    @Test
    void "apply should not configure kdoc without java plugin"() {
        project.with {
            apply plugin: KdocElementsPlugin

            evaluate()

            assertFalse project.plugins.hasPlugin('org.jetbrains.dokka')
        }
    }

    @Test
    void "apply should not configure kdoc with java plugin"() {
        project.with {
            apply plugin: KdocElementsPlugin

            apply plugin: 'java-library'

            evaluate()

            assertNull configurations.findByName('kdocElements')
        }
    }

    @Test
    void "apply should configure kdoc with java extension"() {
        project.with {
            apply plugin: KdocElementsPlugin

            apply plugin: 'java-library'

            java {
                withKdocJar()
            }

            evaluate()

            assertNotNull configurations.kdocElements
        }
    }
}
