//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Named;

import javax.annotation.Nonnull;

import static org.gradle.api.attributes.Usage.JAVA_API;
import static org.gradle.api.attributes.Usage.JAVA_RUNTIME;

enum MavenScope implements Named {

    COMPILE(
            "compile",
            JAVA_API
    ),

    RUNTIME(
            "runtime",
            JAVA_RUNTIME
    );

    private final String name;
    private final String usage;

    MavenScope(String name, String usage) {
        this.name = name;
        this.usage = usage;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }
}
