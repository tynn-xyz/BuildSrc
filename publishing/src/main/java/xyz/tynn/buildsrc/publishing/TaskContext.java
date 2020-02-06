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

    String getDirName() {
        return scope.getName() + '/' + context.getDirName();
    }

    TaskProvider<Jar> getJarProvider() {
        return context.getJarProvider(scope, new JarTask(this));
    }

    TaskProvider<Javadoc> getJavadocTask() {
        return context.getTaskProvider(scope, Javadoc.class, new JavadocTask(this));
    }

    Set<File> getJavaSourceDirectories() {
        return context.getJavaSourceDirectories();
    }

    Action<ConfigurationVariantDetails> getMavenMapping() {
        return new MavenMapping(scope.getMavenScope());
    }

    String getOutputsDir() {
        return scope.getOutputsDir();
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
}
