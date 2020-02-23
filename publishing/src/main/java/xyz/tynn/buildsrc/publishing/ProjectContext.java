//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.UnknownConfigurationException;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import static java.util.Arrays.asList;

final class ProjectContext {

    private final Project project;

    ProjectContext(Project project) {
        this.project = project;
    }

    void applyPlugin(Class<? extends Plugin<? extends Project>> plugin) {
        project.getPluginManager().apply(plugin);
    }

    void withLibraryPlugin(Action<LibraryPlugin> action) {
        project.getPlugins().withType(LibraryPlugin.class, action);
    }

    <T extends Named> void setAttribute(AttributeContainer attributes, Attribute<T> key, String name) {
        attributes.attribute(key, project.getObjects().named(key.getType(), name));
    }

    AdhocComponentWithVariants getComponent(String name, SoftwareComponentFactory softwareComponentFactory) {
        try {
            return getComponent(name);
        } catch (UnknownDomainObjectException e) {
            AdhocComponentWithVariants component = softwareComponentFactory.adhoc(name);
            if (project.getComponents().add(component)) return component;
            return getComponent(name);
        }
    }

    AdhocComponentWithVariants getComponent(String name) {
        return (AdhocComponentWithVariants) project.getComponents().getByName(name);
    }

    Configuration getConfiguration(String name) {
        ConfigurationContainer configurations = project.getConfigurations();
        try {
            return configurations.getByName(name);
        } catch (UnknownConfigurationException e) {
            Configuration configuration = configurations.create(name);
            configuration.setCanBeConsumed(false);
            configuration.setCanBeResolved(false);
            configuration.setVisible(false);
            return configuration;
        }
    }

    Dependency getProjectDependency(MavenPublication publication) {
        StringBuilder dependency = new StringBuilder(publication.getGroupId());
        dependency.append(':').append(publication.getArtifactId());
        dependency.append(':').append(publication.getVersion());
        return project.getDependencies().create(dependency);
    }

    LibraryExtension getLibraryExtension() {
        return project.getExtensions().getByType(LibraryExtension.class);
    }

    Action<LibraryVariant> getMavenAction(SoftwareComponentFactory softwareComponentFactory) {
        return new MavenAction(this, softwareComponentFactory);
    }

    Action<LibraryPlugin> getPluginAction(Action<ProjectContext> action) {
        return new PluginAction(this, action);
    }

    String getProjectGroup() {
        return project.getGroup().toString();
    }

    String getProjectName() {
        return project.getName();
    }

    PublishingExtension getPublishingExtension() {
        return project.getExtensions().getByType(PublishingExtension.class);
    }

    TaskProvider<Task> getTaskProvider(String name, Object... paths) {
        Action<Task> config = task -> task.dependsOn(paths);
        TaskContainer tasks = project.getTasks();
        try {
            return tasks.named(name, config);
        } catch (UnknownTaskException e) {
            return tasks.register(name, config);
        }
    }

    <T extends Task> TaskProvider<T> getTaskProvider(String name, Class<T> type, Action<T> config) {
        TaskContainer tasks = project.getTasks();
        try {
            return tasks.named(name, type);
        } catch (UnknownTaskException e) {
            return tasks.register(name, type, config);
        }
    }

    Action<LibraryVariant> getVariantAction(PublishingScope... scopes) {
        return new VariantAction(this, asList(scopes));
    }

    VariantContext getVariantContext(LibraryVariant variant) {
        return new VariantContext(this, variant);
    }
}
