//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Named;
import org.gradle.api.attributes.DocsType;

import javax.annotation.Nonnull;

import static com.android.builder.model.AndroidProject.FD_OUTPUTS;
import static xyz.tynn.buildsrc.publishing.MavenScope.COMPILE;
import static xyz.tynn.buildsrc.publishing.MavenScope.RUNTIME;

enum ArtifactScope implements Named {

    JAVADOC(
            DocsType.JAVADOC,
            COMPILE,
            TaskContext::getJavadocTask
    ),

    KDOC(
            "kdoc",
            COMPILE,
            TaskContext::getKdocTask
    ),

    SOURCES(
            DocsType.SOURCES,
            RUNTIME,
            TaskContext::getSourceDirectories
    );

    private final String name;
    private final MavenScope mavenScope;
    private final Scoped<?> sourcePath;

    ArtifactScope(String name, MavenScope mavenScope, Scoped<?> sourcePath) {
        this.name = name;
        this.mavenScope = mavenScope;
        this.sourcePath = sourcePath;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    MavenScope getMavenScope() {
        return mavenScope;
    }

    Scoped<?> getSourcePath() {
        return sourcePath;
    }

    String getOutputsDir() {
        return FD_OUTPUTS + '/' + name;
    }
}
