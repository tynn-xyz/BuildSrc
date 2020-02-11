//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing.dokka;

import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.dokka.gradle.DokkaPlugin;

public final class NoDokkaTaskPlugin extends DokkaPlugin {

    @Override
    public void apply(@NotNull Project project) {
        super.apply(new NoDokkaTaskProject(project));
    }
}
