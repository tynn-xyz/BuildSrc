//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.elements

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin

import static org.gradle.api.plugins.internal.JvmPluginsHelper.configureDocumentationVariantWithArtifact as docs

final class KdocElementsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.with {
            plugins.withType(JavaBasePlugin) {
                pluginManager.apply 'org.jetbrains.dokka'
                java.ext.withKdocJar = {
                    docs 'kdocElements',
                            null,
                            "kdoc", [],
                            "kdocJar",
                            tasks.named('dokkaHtml'),
                            components.java,
                            configurations,
                            tasks,
                            objects
                }
            }
        }
    }
}
