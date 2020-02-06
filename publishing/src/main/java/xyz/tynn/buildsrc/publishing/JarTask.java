//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Action;
import org.gradle.jvm.tasks.Jar;

import java.io.File;

final class JarTask implements Action<Jar> {

    private final TaskContext context;
    private final Object sourcePath;

    JarTask(TaskContext context) {
        this.context = context;
        this.sourcePath = context.getSourcePath();
    }

    @Override
    public void execute(Jar jar) {
        File buildDir = jar.getProject().getBuildDir();
        File destinationDir = new File(buildDir, context.getOutputsDir());
        jar.getDestinationDirectory().set(destinationDir);
        jar.getArchiveClassifier().set(context.getArtifactClassifier());
        jar.from(sourcePath);
    }
}
