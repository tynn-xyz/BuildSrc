//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Action;
import org.jetbrains.dokka.gradle.DokkaTask;

import java.io.File;

import static java.util.Collections.singletonList;

final class KdocTask implements Action<DokkaTask> {

    private final TaskContext context;

    KdocTask(TaskContext context) {
        this.context = context;
    }

    @Override
    public void execute(DokkaTask dokka) {
        File buildDir = dokka.getProject().getBuildDir();
        dokka.setOutputFormat("html");
        dokka.setOutputDirectory(new File(buildDir, context.getDirName()).getAbsolutePath());
        dokka.getConfiguration().setAndroidVariants(singletonList(context.getVariantName()));
    }
}
