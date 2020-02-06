//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.SoftwareComponentContainer;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

import static java.util.Arrays.asList;

final class ProjectContext {

    private final SoftwareComponentContainer components;
    private final ConfigurationContainer configurations;
    private final ExtensionContainer extensions;
    private final ObjectFactory objects;
    private final PluginContainer plugins;
    private final TaskContainer tasks;

    ProjectContext(Project project) {
        this.components = project.getComponents();
        this.configurations = project.getConfigurations();
        this.extensions = project.getExtensions();
        this.objects = project.getObjects();
        this.plugins = project.getPlugins();
        this.tasks = project.getTasks();
    }

    void withLibraryPlugin(Action<LibraryPlugin> action) {
        plugins.withType(LibraryPlugin.class, action);
    }

    <T extends Named> void setAttribute(AttributeContainer attributes, Attribute<T> key, String name) {
        attributes.attribute(key, objects.named(key.getType(), name));
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

    Action<LibraryPlugin> getPluginAction(Action<LibraryVariant> action) {
        return new PluginAction(this, action);
    }

    TaskProvider<Task> getTaskProvider(String name, Object... paths) {
        Action<Task> config = task -> task.dependsOn(paths);
        try {
            return tasks.named(name, config);
        } catch (UnknownTaskException e) {
            return tasks.register(name, config);
        }
    }

    <T extends Task> TaskProvider<T> getTaskProvider(String name, Class<T> type, Action<T> config) {
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
