//  Copyright 2021 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.elements

import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.extra

fun JavaPluginExtension.withKdocJar() {
    val extensions = this as ExtensionAware
    val delegate = extensions.extra["withKdocJar"] as? Runnable
    delegate?.run()
}
