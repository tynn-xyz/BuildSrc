//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Transformer;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.ConfigurationVariantDetails;
import org.gradle.api.component.SoftwareComponentContainer;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.jvm.tasks.Jar;

import java.util.List;

import static java.util.Arrays.asList;

final class ProjectContext {

    private final SoftwareComponentContainer components;
    private final ConfigurationContainer configurations;
    private final ExtensionContainer extensions;
    private final ObjectFactory objects;
    private final PluginContainer plugins;
    private final TaskContainer tasks;

    private final MavenMapping mavenMapping;

    ProjectContext(Project project) {
        this.components = project.getComponents();
        this.configurations = project.getConfigurations();
        this.extensions = project.getExtensions();
        this.objects = project.getObjects();
        this.plugins = project.getPlugins();
        this.tasks = project.getTasks();
        this.mavenMapping = new MavenMapping();
    }

    void withLibraryPlugin(Action<LibraryPlugin> action) {
        plugins.withType(LibraryPlugin.class, action);
    }

    Action<AttributeContainer> getArtifactAttributes(List<Action<AttributeContainer>> attributes) {
        return new ArtifactAttributes(attributes);
    }

    TaskProvider<Jar> getArtifactJar(String name) throws UnknownTaskException {
        return tasks.named(name, Jar.class);
    }

    TaskProvider<Jar> getArtifactJar(String name, Action<Jar> config) {
        return buildDependsOn(tasks.register(name, Jar.class, config));
    }

    Configuration getConfiguration(String name) {
        return configurations.maybeCreate(name);
    }

    AdhocComponentWithVariants getComponent(String name) {
        return (AdhocComponentWithVariants) components.getByName(name);
    }

    LibraryExtension getLibraryExtension() {
        return extensions.getByType(LibraryExtension.class);
    }

    Action<ConfigurationVariantDetails> getMavenMapping() {
        return mavenMapping;
    }

    Action<AttributeContainer> getFlavorAttributes(LibraryVariant variant) {
        return new FlavorAttributes(variant, objects);
    }

    Action<LibraryPlugin> getPluginAction(Action<LibraryVariant> action) {
        return new PluginAction(this, action);
    }

    Action<AttributeContainer> getSourcesAttributes() {
        return new SourcesAttributes(objects);
    }

    Action<Jar> getSourcesJar(LibraryVariant variant, String destinationDir) {
        return new SourcesJar(variant, destinationDir);
    }

    Action<LibraryVariant> getVariantAction(ArtifactScope... scopes) {
        return new VariantAction(this, asList(scopes));
    }

    Transformer<PublishArtifact, AbstractArchiveTask> getVariantArtifact(String classifier) {
        return new VariantArtifact(classifier);
    }

    Action<AttributeContainer> getVariantAttributes(LibraryVariant variant) {
        return new VariantAttributes(variant, objects);
    }

    VariantContext getVariantContext(LibraryVariant variant) {
        return new VariantContext(this, variant);
    }

    <T> T buildDependsOn(T path) {
        tasks.named("build").configure(task -> task.dependsOn(path));
        return path;
    }
}
