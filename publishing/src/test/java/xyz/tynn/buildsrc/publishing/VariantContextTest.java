package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.Transformer;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.jvm.tasks.Jar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
@ExtendWith(MockitoExtension.class)
class VariantContextTest {

    @Mock
    ProjectContext projectContext;
    @Mock
    LibraryVariant variant;

    @Mock
    ArtifactScope scope;

    @InjectMocks
    VariantContext context;

    @Test
    void getConfigurationShouldShouldDelegateToProjectContext() {
        String name = "name";
        String configurationName = "configurationName";
        Configuration configuration = mock(Configuration.class);
        doReturn(configuration).when(projectContext).getConfiguration(any());
        doReturn(configurationName).when(scope).getPublicationName(any());
        doReturn(name).when(variant).getName();

        assertEquals(configuration, context.getConfiguration(scope));
        verify(projectContext).getConfiguration(configurationName);
        verify(scope).getPublicationName(name);
        verify(variant).getName();
    }

    @Test
    void getArtifactAttributesShouldDelegateToProjectContext() {
        List<Action<AttributeContainer>> attributeList = mock(List.class);
        Action<AttributeContainer> attributes = mock(Action.class);
        doReturn(attributes).when(projectContext).getArtifactAttributes(any());
        doReturn(attributeList).when(scope).getArtifactAttributes(any());

        assertEquals(attributes, context.getArtifactAttributes(scope));
        verify(projectContext).getArtifactAttributes(attributeList);
        verify(scope).getArtifactAttributes(context);
    }

    @Test
    void getArtifactJarShouldReturnNamedTask() {
        String name = "name";
        String jarName = "jarName";
        TaskProvider<Jar> task = mock(TaskProvider.class);
        doReturn(task).when(projectContext).getArtifactJar(any());
        doReturn(jarName).when(scope).getJarName(any());
        doReturn(name).when(variant).getName();

        assertEquals(task, context.getArtifactJar(scope));
        verify(projectContext).getArtifactJar(jarName);
        verify(scope).getJarName(name);
        verify(variant).getName();
    }

    @Test
    void getArtifactJarShouldRegisterTask() {
        String name = "name";
        String jarName = "jarName";
        Action<Jar> config = mock(Action.class);
        TaskProvider<Jar> task = mock(TaskProvider.class);
        doThrow(new UnknownTaskException("")).when(projectContext).getArtifactJar(any());
        doReturn(task).when(projectContext).getArtifactJar(any(), any());
        doReturn(config).when(scope).getJarConfig(any());
        doReturn(jarName).when(scope).getJarName(any());
        doReturn(name).when(variant).getName();

        assertEquals(task, context.getArtifactJar(scope));
        verify(projectContext).getArtifactJar(jarName, config);
        verify(scope).getJarConfig(context);
        verify(scope).getJarName(name);
        verify(variant).getName();
    }

    @Test
    void getComponentShouldShouldDelegateToProjectContext() {
        String name = "name";
        String componentName = "componentName";
        AdhocComponentWithVariants component = mock(AdhocComponentWithVariants.class);
        doReturn(component).when(projectContext).getComponent(any());
        doReturn(componentName).when(scope).getComponentName(any());
        doReturn(name).when(variant).getName();

        assertEquals(component, context.getComponent(scope));
        verify(projectContext).getComponent(componentName);
        verify(scope).getComponentName(name);
        verify(variant).getName();
    }

    @Test
    void getFlavorAttributesShouldDelegateToProjectContext() {
        Action<AttributeContainer> attributes = mock(Action.class);
        doReturn(attributes).when(projectContext).getFlavorAttributes(any());

        assertEquals(attributes, context.getFlavorAttributes());
        verify(projectContext).getFlavorAttributes(variant);
    }

    @Test
    void getSourcesAttributesShouldDelegateToProjectContext() {
        Action<AttributeContainer> attributes = mock(Action.class);
        doReturn(attributes).when(projectContext).getSourcesAttributes();

        assertEquals(attributes, context.getSourcesAttributes());
        verify(projectContext).getSourcesAttributes();
    }

    @Test
    void getSourcesJarShouldDelegateToProjectContext() {
        String destinationDir = "dest";
        Action<Jar> config = mock(Action.class);
        doReturn(config).when(projectContext).getSourcesJar(any(), any());

        assertEquals(config, context.getSourcesJar(destinationDir));
        verify(projectContext).getSourcesJar(variant, destinationDir);
    }

    @Test
    void getVariantArtifactShouldDelegateToProjectContext() {
        String name = "name";
        String classifier = "classifier";
        Transformer<PublishArtifact, AbstractArchiveTask> artifact = mock(Transformer.class);
        doReturn(artifact).when(projectContext).getVariantArtifact(any());
        doReturn(classifier).when(scope).getArtifactClassifier(any());
        doReturn(name).when(variant).getName();

        assertEquals(artifact, context.getVariantArtifact(scope));
        verify(projectContext).getVariantArtifact(classifier);
        verify(scope).getArtifactClassifier(name);
        verify(variant).getName();
    }

    @Test
    void getVariantAttributesShouldDelegateToProjectContext() {
        Action<AttributeContainer> attributes = mock(Action.class);
        doReturn(attributes).when(projectContext).getVariantAttributes(any());

        assertEquals(attributes, context.getVariantAttributes());
        verify(projectContext).getVariantAttributes(variant);
    }
}
