//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing

import org.gradle.api.InvalidUserCodeException
import org.gradle.api.Project
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

import static org.gradle.testfixtures.ProjectBuilder.builder
import static org.junit.jupiter.api.Assertions.*
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.*

@ExtendWith(MockitoExtension.class)
class AndroidJavadocPluginTest {

    @Test
    void "apply should not run without android plugins"() {
        AndroidJavadocPlugin plugin = spy(AndroidJavadocPlugin)
        doAnswer {
            ProjectContext context = spy(new ProjectContext(it.getArgument(0, Project.class)))
            doThrow(new NoClassDefFoundError()).when(context).getPluginAction(any())
            context
        }.when(plugin).getProjectContext(any())

        assertThrows InvalidUserCodeException, {
            plugin.apply builder().build()
        }
    }

    @Test
    void "apply should not configure javadoc without android library plugin"() {
        builder().build().with {
            apply plugin: AndroidJavadocPlugin

            evaluate()

            assertTrue configurations.findAll { name =~ /JavadocPublication$/ }.empty
        }
    }

    @Test
    void "apply should configure javadoc with android library plugin"() {
        builder().build().with {
            apply plugin: AndroidJavadocPlugin

            apply plugin: 'com.android.library'
            android.compileSdkVersion = 31

            evaluate()

            assertNotNull configurations.debugJavadocPublication
            assertNotNull configurations.releaseJavadocPublication
            assertNotNull configurations.debugAllJavadocPublication
            assertNotNull configurations.releaseAllJavadocPublication
        }
    }
}
