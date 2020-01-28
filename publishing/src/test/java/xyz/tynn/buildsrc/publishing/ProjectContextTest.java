//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.SoftwareComponentContainer;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Answers.RETURNS_MOCKS;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"unchecked", "WeakerAccess"})
@ExtendWith(MockitoExtension.class)
class ProjectContextTest {

    @Mock(answer = RETURNS_MOCKS)
    LibraryVariant variant;
    @Mock
    Project project;
    @Mock
    SoftwareComponentContainer components;
    @Mock
    ConfigurationContainer configurations;
    @Mock
    ExtensionContainer extensions;
    @Mock
    ObjectFactory objects;
    @Mock
    PluginContainer plugins;
    @Mock
    TaskContainer tasks;

    ProjectContext context;

    @BeforeEach
    void setup() {
        doReturn(components).when(project).getComponents();
        doReturn(configurations).when(project).getConfigurations();
        doReturn(extensions).when(project).getExtensions();
        doReturn(objects).when(project).getObjects();
        doReturn(plugins).when(project).getPlugins();
        doReturn(tasks).when(project).getTasks();

        context = spy(new ProjectContext(project));
    }

    @Test
    void withLibraryPluginShouldRunActionWithPluginType() {
        Action<LibraryPlugin> action = mock(Action.class);

        context.withLibraryPlugin(action);

        verify(plugins).withType(LibraryPlugin.class, action);
    }

    @Test
    void getArtifactAttributesShouldReturn() {
        assertTrue(context.getArtifactAttributes(emptyList()) instanceof ArtifactAttributes);
    }

    @Test
    void getArtifactJarShouldReturnNamedJarTask() {
        String name = "name";
        TaskProvider task = mock(TaskProvider.class);
        doReturn(task).when(tasks).named(any(), any(Class.class));

        assertEquals(task, context.getArtifactJar(name));
        verify(tasks).named(name, Jar.class);
    }

    @Test
    void getArtifactJarShouldRegisterJarTask() {
        String name = "name";
        TaskProvider task = mock(TaskProvider.class);
        Action config = mock(Action.class);
        doAnswer(returnsFirstArg()).when(context).buildDependsOn(any());
        doReturn(task).when(tasks).register(any(), any(), any(Action.class));

        assertEquals(task, context.getArtifactJar(name, config));
        verify(context).buildDependsOn(task);
        verify(tasks).register(name, Jar.class, config);
    }

    @Test
    void getConfigurationShouldMaybeCreateConfiguration() {
        String name = "configurationName";
        Configuration configuration = mock(Configuration.class);
        doReturn(configuration).when(configurations).maybeCreate(any());

        assertEquals(configuration, context.getConfiguration(name));
        verify(configurations).maybeCreate(name);
    }

    @Test
    void getComponentShouldGetAdhocComponentWithVariantsByName() {
        String name = "componentName";
        AdhocComponentWithVariants component = mock(AdhocComponentWithVariants.class);
        doReturn(component).when(components).getByName(any());

        assertEquals(component, context.getComponent(name));
        verify(components).getByName(name);
    }

    @Test
    void getLibraryExtensionShouldGetExtensionByType() {
        LibraryExtension extension = mock(LibraryExtension.class);
        doReturn(extension).when(extensions).getByType(any(Class.class));

        assertEquals(extension, context.getLibraryExtension());
        verify(extensions).getByType(LibraryExtension.class);
    }

    @Test
    void getMavenMappingShouldReturnMavenMapping() {
        assertTrue(context.getMavenMapping() instanceof MavenMapping);
    }

    @Test
    void getFlavorAttributesShouldReturnFlavorAttributes() {
        assertTrue(context.getFlavorAttributes(variant) instanceof FlavorAttributes);
    }

    @Test
    void getPluginActionShouldReturnPluginAction() {
        assertTrue(context.getPluginAction(mock(Action.class)) instanceof PluginAction);
    }

    @Test
    void getSourcesAttributesShouldReturnSourcesAttributes() {
        assertTrue(context.getSourcesAttributes() instanceof SourcesAttributes);
    }

    @Test
    void getSourcesJarShouldReturnSourcesJar() {
        assertTrue(context.getSourcesJar(variant, "path") instanceof SourcesJar);
    }

    @Test
    void getSourcesActionShouldReturnSourcesAction() {
        assertTrue(context.getVariantAction() instanceof VariantAction);
    }

    @Test
    void getVariantArtifactShouldReturnVariantArtifact() {
        assertTrue(context.getVariantArtifact(null) instanceof VariantArtifact);
    }

    @Test
    void getVariantAttributesShouldReturnVariantAttributes() {
        assertTrue(context.getVariantAttributes(variant) instanceof VariantAttributes);
    }

    @Test
    void getVariantContextShouldReturnVariantContext() {
        assertNotNull(context.getVariantContext(variant));
    }

    @Test
    void buildDependsOnShouldReturnPath() {
        doAnswer(RETURNS_DEEP_STUBS).when(tasks).named(any());

        assertEquals("path", context.buildDependsOn("path"));
    }

    @Test
    void buildDependsOnShouldConfigureDependsOnPath() {
        Task task = mock(Task.class);
        TaskProvider<?> provider = mock(TaskProvider.class);
        doReturn(provider).when(tasks).named(any());

        context.buildDependsOn(provider);

        ArgumentCaptor<Action> action = forClass(Action.class);
        verify(provider).configure(action.capture());
        action.getValue().execute(task);
        verify(task).dependsOn(provider);
    }
}
