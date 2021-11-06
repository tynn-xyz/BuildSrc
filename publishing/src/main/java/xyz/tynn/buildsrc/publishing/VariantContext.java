//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import static org.gradle.api.attributes.Attribute.of;
import static xyz.tynn.buildsrc.publishing.ZeroUtils.join;
import static xyz.tynn.buildsrc.publishing.ZeroUtils.joinCapitalized;

import com.android.build.api.attributes.BuildTypeAttr;
import com.android.build.api.attributes.ProductFlavorAttr;
import com.android.build.gradle.api.LibraryVariant;
import com.android.builder.model.BaseConfig;
import com.android.builder.model.ProductFlavor;
import com.android.builder.model.SourceProvider;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.Task;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.internal.HasConvention;
import org.gradle.api.plugins.Convention;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.jvm.tasks.Jar;
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class VariantContext {

    private final ProjectContext context;
    private final LibraryVariant variant;

    VariantContext(ProjectContext context, LibraryVariant variant) {
        this.context = context;
        this.variant = variant;
    }

    void connectAssembleTasks(TaskProvider<?> provider) {
        String flavorName = variant.getFlavorName();
        if (!flavorName.isEmpty())
            context.getTaskProvider(joinCapitalized("assemble", flavorName), provider);
        context.getTaskProvider(joinCapitalized("assemble", variant.getBuildType()), provider);
        for (ProductFlavor productFlavor : variant.getProductFlavors())
            context.getTaskProvider(joinCapitalized("assemble", productFlavor), provider);
    }

    <T extends Named> void setAttribute(AttributeContainer attributes, Attribute<T> key, String name) {
        context.setAttribute(attributes, key, name);
    }

    void setBuildTypeAttribute(AttributeContainer attributes) {
        context.setAttribute(attributes, BuildTypeAttr.ATTRIBUTE, variant.getBuildType().getName());
    }

    void setProductFlavorAttributes(AttributeContainer attributes) {
        for (ProductFlavor flavor : variant.getProductFlavors()) {
            @SuppressWarnings("ConstantConditions")
            Attribute<ProductFlavorAttr> attr = of(flavor.getDimension(), ProductFlavorAttr.class);
            context.setAttribute(attributes, attr, flavor.getName());
        }
    }

    File getBuildDir(String child) {
        return new File(context.getBuildDir(), child);
    }

    Configuration getConfiguration(PublishingScope scope) {
        return context.getConfiguration(scope.getPublicationName(variant.getName()));
    }

    String getArtifactClassifier() {
        ArrayList<String> parts = new ArrayList<>();
        for (BaseConfig config : variant.getProductFlavors())
            parts.add(config.getName());
        parts.add(variant.getBuildType().getName());
        return join(parts);
    }

    TaskProvider<Jar> getJarProvider(PublishingScope scope, Action<Jar> config) {
        String name = joinCapitalized(variant.getName(), scope.getName(), "jar");
        TaskProvider<Jar> provider = context.getTaskProvider(name, Jar.class, config);
        context.getTaskProvider("build", provider);
        return provider;
    }

    <T extends Task> TaskProvider<T> getTaskProvider(PublishingScope scope, Class<T> type, Action<T> config) {
        return context.getTaskProvider(joinCapitalized(variant.getName(), scope.getName()), type, config);
    }

    List<File> getBootClasspath() {
        return context.getLibraryExtension().getBootClasspath();
    }

    AdhocComponentWithVariants getComponent(PublishingScope scope) {
        return context.getComponent(scope.getComponentName(variant.getName()));
    }

    String getDirName() {
        return variant.getDirName();
    }

    Set<File> getJavaSourceDirectories() {
        HashSet<File> sourceDirectories = new HashSet<>();
        for (SourceProvider sourceSet : variant.getSourceSets())
            sourceDirectories.addAll(sourceSet.getJavaDirectories());
        return sourceDirectories;
    }

    Set<File> getKotlinSourceDirectories() {
        HashSet<File> sourceDirectories = new HashSet<>();
        for (SourceProvider sourceSet : variant.getSourceSets()) {
            try {
                Convention convention = ((HasConvention) sourceSet).getConvention();
                KotlinSourceSet kotlinSourceSet = convention.getPlugin(KotlinSourceSet.class);
                sourceDirectories.addAll(kotlinSourceSet.getKotlin().getSrcDirs());
            } catch (ClassCastException | IllegalStateException | NoClassDefFoundError ignored) {
            }
        }
        return sourceDirectories;
    }

    TaskContext getTaskContext(PublishingScope scope) {
        return new TaskContext(this, scope);
    }

    Transformer<PublishArtifact, AbstractArchiveTask> getVariantArtifact(PublishingScope scope) {
        return new VariantArtifact(scope.getArtifactClassifier(variant.getName()));
    }

    boolean containsSourceSet(String name) {
        for (SourceProvider source : variant.getSourceSets())
            if (source.getName().equals(name)) return true;
        return false;
    }
}
