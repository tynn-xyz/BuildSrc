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
class AndroidSourcesPluginTest {

    @Test
    void "apply should not run without android plugins"() {
        AndroidSourcesPlugin plugin = spy(AndroidSourcesPlugin)
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
    void "apply should not configure sources without android library plugin"() {
        builder().build().with {
            apply plugin: AndroidSourcesPlugin

            evaluate()

            assertTrue configurations.findAll { name =~ /SourcesPublication$/ }.empty
        }
    }

    @Test
    void "apply should configure sources with android library plugin"() {
        builder().build().with {
            apply plugin: AndroidSourcesPlugin

            apply plugin: 'com.android.library'
            android.compileSdkVersion = 31

            evaluate()

            assertNotNull configurations.debugSourcesPublication
            assertNotNull configurations.releaseSourcesPublication
            assertNotNull configurations.debugAllSourcesPublication
            assertNotNull configurations.releaseAllSourcesPublication
        }
    }
}
