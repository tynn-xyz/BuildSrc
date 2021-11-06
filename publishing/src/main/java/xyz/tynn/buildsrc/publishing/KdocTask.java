//  Copyright 2021 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Action;
import org.jetbrains.dokka.gradle.DokkaTask;
import org.jetbrains.dokka.gradle.GradleDokkaSourceSetBuilder;

final class KdocTask implements Action<DokkaTask> {

    private final TaskContext context;

    KdocTask(TaskContext context) {
        this.context = context;
    }

    @Override
    public void execute(DokkaTask dokka) {
        dokka.getOutputDirectory().set(context.getOutputDirectory());
        dokka.getDokkaSourceSets().all(this::suppressNonVariantSourceSets);
    }

    private void suppressNonVariantSourceSets(GradleDokkaSourceSetBuilder builder) {
        if (!context.containsSourceSet(builder.getName())) builder.getSuppress().set(true);
    }
}
