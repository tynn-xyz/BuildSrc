//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Action;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.ConfigurationVariantDetails;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.jvm.tasks.Jar;
import org.jetbrains.dokka.gradle.DokkaTask;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class TaskContext {

    private final VariantContext context;
    private final PublishingScope scope;

    TaskContext(VariantContext context, PublishingScope scope) {
        this.context = context;
        this.scope = scope;
    }

    Action<AttributeContainer> getArtifactAttributes() {
        return new ArtifactAttributes(context, scope);
    }

    String getArtifactClassifier() {
        return context.getArtifactClassifier();
    }

    List<File> getBootClasspath() {
        return context.getBootClasspath();
    }

    AdhocComponentWithVariants getComponent() {
        return context.getComponent(scope);
    }

    Configuration getConfiguration() {
        return context.getConfiguration(scope);
    }

    File getOutputDirectory() {
        return new File(context.getBuildDir(scope.getName()), context.getDirName());
    }

    File getOutputsDir() {
        return context.getBuildDir(scope.getOutputsDir());
    }

    TaskProvider<Jar> getJarProvider() {
        return context.getJarProvider(scope, new JarTask(this));
    }

    Set<File> getJavaSourceDirectories() {
        return context.getJavaSourceDirectories();
    }

    TaskProvider<Javadoc> getJavadocTask() {
        return context.getTaskProvider(scope, Javadoc.class, new JavadocTask(this));
    }

    TaskProvider<DokkaTask> getKdocTask() {
        return context.getTaskProvider(scope, DokkaTask.class, new KdocTask(this));
    }

    Action<ConfigurationVariantDetails> getMavenMapping() {
        return new MavenMapping(scope.getMavenScope());
    }

    Transformer<PublishArtifact, AbstractArchiveTask> getPublishArtifact() {
        return context.getVariantArtifact(scope);
    }

    Action<AttributeContainer> getPublishingAttributes() {
        return scope.getPublishingAttributes().apply(this);
    }

    Set<File> getSourceDirectories() {
        HashSet<File> sourceDirectories = new HashSet<>();
        sourceDirectories.addAll(context.getJavaSourceDirectories());
        sourceDirectories.addAll(context.getKotlinSourceDirectories());
        return sourceDirectories;
    }

    Object getSourcePath() {
        return scope.getSourcePath().apply(this);
    }

    Action<AttributeContainer> getVariantAttributes() {
        return new VariantAttributes(context, scope);
    }

    boolean containsSourceSet(String name) {
        return context.containsSourceSet(name);
    }
}
