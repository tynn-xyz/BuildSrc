//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.LibraryPlugin;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.ivy.plugins.IvyPublishPlugin;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.lang.String.valueOf;
import static org.gradle.api.attributes.Attribute.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class ProjectContextTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    Project project;

    @InjectMocks
    ProjectContext context;

    @Test
    void applyPluginShouldApplyPlugin() {
        context.applyPlugin(IvyPublishPlugin.class);

        verify(project.getPluginManager()).apply(IvyPublishPlugin.class);
    }

    @Test
    void withLibraryPluginShouldRunActionWithPluginType() {
        Action<LibraryPlugin> action = mock(Action.class);

        context.withLibraryPlugin(action);

        verify(project.getPlugins()).withType(LibraryPlugin.class, action);
    }

    @Test
    void setAttributeShouldSetNamedAttribute() {
        String name = "name";
        Named value = () -> name;
        Attribute<Named> key = of(Named.class);
        AttributeContainer attributes = mock(AttributeContainer.class);
        when(project.getObjects().named(Named.class, name)).thenReturn(value);

        context.setAttribute(attributes, key, name);

        verify(attributes).attribute(key, value);
    }

    @Test
    void getConfigurationShouldMaybeCreateConfiguration() {
        String name = "configurationName";
        Configuration configuration = mock(Configuration.class);
        when(project.getConfigurations().maybeCreate(name)).thenReturn(configuration);

        assertEquals(configuration, context.getConfiguration(name));
    }

    @Test
    void getComponentShouldGetAdhocComponentWithVariantsByName() {
        String name = "componentName";
        AdhocComponentWithVariants component = mock(AdhocComponentWithVariants.class);
        when(project.getComponents().getByName(name)).thenReturn(component);

        assertEquals(component, context.getComponent(name));
    }

    @Test
    void getLibraryExtensionShouldGetExtensionByType() {
        LibraryExtension extension = mock(LibraryExtension.class);
        when(project.getExtensions().getByType(LibraryExtension.class)).thenReturn(extension);

        assertEquals(extension, context.getLibraryExtension());
    }

    @Test
    void getMavenActionShouldReturnMavenAction() {
        assertTrue(context.getMavenAction() instanceof MavenAction);
    }

    @Test
    void getPluginActionShouldReturnPluginAction() {
        assertTrue(context.getPluginAction(mock(Action.class)) instanceof PluginAction);
    }

    @Test
    void getProjectGroupShouldReturnProjectGroup() {
        int group = 5;
        when(project.getGroup()).thenReturn(group);

        assertEquals(valueOf(group), context.getProjectGroup());
    }

    @Test
    void getProjectNameShouldReturnProjectName() {
        String name = "name";
        when(project.getName()).thenReturn(name);

        assertEquals(name, context.getProjectName());
    }

    @Test
    void getPublishingExtensionShouldGetExtensionByType() {
        PublishingExtension extension = mock(PublishingExtension.class);
        when(project.getExtensions().getByType(PublishingExtension.class)).thenReturn(extension);

        assertEquals(extension, context.getPublishingExtension());
    }

    @Test
    void getSourcesActionShouldReturnSourcesAction() {
        assertTrue(context.getVariantAction() instanceof VariantAction);
    }

    @Test
    void getTaskProviderShouldReturnNamedTask() {
        String name = "name";
        TaskProvider<Jar> task = mock(TaskProvider.class);
        when(project.getTasks().named(name, Jar.class)).thenReturn(task);

        assertEquals(task, context.getTaskProvider(name, Jar.class, null));
    }

    @Test
    void getTaskProviderShouldReturnNamedTaskWithDependency() {
        String name = "name";
        String path = "path";
        TaskProvider<Task> task = mock(TaskProvider.class, RETURNS_DEEP_STUBS);
        when(project.getTasks().named(any(), any(Action.class))).thenReturn(task);

        assertEquals(task, context.getTaskProvider(name, path));

        ArgumentCaptor<Action<Task>> action = forClass(Action.class);
        verify(project.getTasks()).named(eq(name), action.capture());
        action.getValue().execute(task.get());
        verify(task.get()).dependsOn(path);
    }

    @Test
    void getTaskProviderShouldRegisterTaskWithConfig() {
        String name = "name";
        TaskProvider<Jar> task = mock(TaskProvider.class);
        Action<Jar> config = mock(Action.class);
        when(project.getTasks().named(any(), any(Class.class))).thenThrow(UnknownTaskException.class);
        when(project.getTasks().register(name, Jar.class, config)).thenReturn(task);

        assertEquals(task, context.getTaskProvider(name, Jar.class, config));
    }

    @Test
    void getTaskProviderShouldRegisterTaskWithDependency() {
        String name = "name";
        String path = "path";
        TaskProvider<Task> task = mock(TaskProvider.class, RETURNS_DEEP_STUBS);
        when(project.getTasks().named(any(), any(Action.class))).thenThrow(UnknownTaskException.class);
        when(project.getTasks().register(any(), any(Action.class))).thenReturn(task);

        assertEquals(task, context.getTaskProvider(name, path));

        ArgumentCaptor<Action<Task>> action = forClass(Action.class);
        verify(project.getTasks()).register(eq(name), action.capture());
        action.getValue().execute(task.get());
        verify(task.get()).dependsOn(path);
    }

    @Test
    void getVariantContextShouldReturnVariantContext() {
        assertNotNull(context.getVariantContext(null));
    }
}
