package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.InvalidUserCodeException;
import org.gradle.api.Transformer;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.artifacts.PublishArtifactSet;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.jvm.tasks.Jar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"ResultOfMethodCallIgnored", "WeakerAccess"})
@ExtendWith(MockitoExtension.class)
class VariantActionTest {

    @Mock
    ProjectContext projectContext;
    @Mock
    ArtifactScope scope;

    @Mock
    LibraryVariant variant;
    @Mock
    VariantContext context;
    @Mock
    TaskProvider<Jar> task;
    @Mock
    Configuration configuration;
    @Mock
    AdhocComponentWithVariants component;
    @Mock
    MavenMapping mapping;
    @Mock
    Action<AttributeContainer> action;
    @Mock
    Transformer<PublishArtifact, AbstractArchiveTask> transformer;
    @Mock
    Provider<PublishArtifact> provider;
    @Mock
    PublishArtifactSet artifacts;

    VariantAction variantAction;

    @BeforeEach
    void setup() {
        variantAction = new VariantAction(projectContext, singletonList(scope));
        lenient().doReturn(context).when(projectContext).getVariantContext(any());
        lenient().doReturn(task).when(context).getArtifactJar(any());
        lenient().doReturn(configuration).when(context).getConfiguration(any());
        lenient().doReturn(component).when(context).getComponent(any());
        lenient().doReturn(artifacts).when(configuration).getArtifacts();
    }

    @Test
    void executeShouldAddConfigurationAttributes() {
        doReturn(action).when(context).getArtifactAttributes(any());

        variantAction.execute(variant);

        verify(configuration).attributes(action);
        verify(context).getArtifactAttributes(scope);
        verify(context).getConfiguration(scope);
        verify(projectContext).getVariantContext(variant);
    }

    @Test
    void executeShouldAddConfigurationArtifact() {
        doReturn(transformer).when(context).getVariantArtifact(any());
        doReturn(provider).when(task).map(any());

        variantAction.execute(variant);

        verify(artifacts).addLater(provider);
        verify(configuration).getArtifacts();
        verify(task).map(transformer);
        verify(context).getConfiguration(scope);
        verify(context).getArtifactJar(scope);
        verify(context).getVariantArtifact(scope);
        verify(projectContext).getVariantContext(variant);
    }

    @Test
    void executeShouldSetConfigurationToComponent() {
        doReturn(mapping).when(projectContext).getMavenMapping();

        variantAction.execute(variant);

        verify(component).addVariantsFromConfiguration(configuration, mapping);
        verify(context).getConfiguration(scope);
        verify(context).getComponent(scope);
        verify(projectContext).getMavenMapping();
        verify(projectContext).getVariantContext(variant);
    }

    @Test
    void executeShouldThrowInvalidUserCodeExceptionOnUnknownDomainObjectException() {
        doThrow(new UnknownDomainObjectException("")).when(projectContext).getVariantContext(any());

        assertThrows(InvalidUserCodeException.class, () -> variantAction.execute(variant));
    }
}
