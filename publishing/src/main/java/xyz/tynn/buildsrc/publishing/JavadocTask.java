//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.MinimalJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

import java.io.File;

final class JavadocTask implements Action<Javadoc> {

    private final TaskContext context;

    JavadocTask(TaskContext context) {
        this.context = context;
    }

    @Override
    public void execute(Javadoc javadoc) {
        Project project = javadoc.getProject();
        javadoc.setDestinationDir(new File(project.getBuildDir(), context.getDirName()));
        javadoc.setSource(context.getJavaSourceDirectories());
        javadoc.getClasspath().plus(project.files(context.getBootClasspath()));
        MinimalJavadocOptions options = javadoc.getOptions();
        if (options instanceof StandardJavadocDocletOptions)
            ((StandardJavadocDocletOptions) options).links(
                    "https://developer.android.com/reference/"
            );
    }
}
