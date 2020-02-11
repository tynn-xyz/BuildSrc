//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing

import org.gradle.api.InvalidUserCodeException
import org.gradle.api.Project
import org.gradle.api.plugins.MavenPlugin
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension

import static org.gradle.testfixtures.ProjectBuilder.builder
import static org.junit.jupiter.api.Assertions.*
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.*

@ExtendWith(MockitoExtension.class)
class AndroidMavenPluginTest {

    @Spy
    AndroidMavenPlugin plugin

    @Test
    void "apply should not run without android plugins"() {
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
    void "apply should not configure publications without android library plugin"() {
        builder().build().with {
            plugin.apply it

            evaluate()

            assertFalse plugins.hasPlugin(MavenPlugin)
        }
    }

    @Test
    void "apply should configure release publications with android library plugin"() {
        builder().build().with {
            plugin.apply it

            apply plugin: 'com.android.library'
            android {
                compileSdkVersion = 29
                flavorDimensions 'app', 'env'
                productFlavors {
                    open { dimension 'app' }
                    closed { dimension 'app' }
                    source { dimension 'env' }
                }
            }

            evaluate()

            assertNotNull publishing.publications.openSourceRelease
            assertNotNull publishing.publications.closedSourceRelease
        }
    }
}
