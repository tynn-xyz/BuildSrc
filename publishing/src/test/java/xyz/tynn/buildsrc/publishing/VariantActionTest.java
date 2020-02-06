//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.InvalidUserCodeException;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.provider.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@ExtendWith(MockitoExtension.class)
class VariantActionTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    ProjectContext projectContext;
    @Mock(answer = RETURNS_DEEP_STUBS)
    TaskContext context;

    @Mock
    Provider<PublishArtifact> provider;
    @Mock
    PublishingScope scope;
    @Mock
    LibraryVariant variant;

    VariantAction variantAction;

    @BeforeEach
    void setup() {
        variantAction = spy(new VariantAction(projectContext, singletonList(scope)));
    }

    @Test
    void executeShouldPrepareConfigurations() {
        doNothing().when(variantAction).prepareConfiguration(any());
        when(projectContext.getVariantContext(variant).getTaskContext(scope)).thenReturn(context);

        variantAction.execute(variant);

        verify(variantAction).prepareConfiguration(context);
    }

    @Test
    void executeShouldThrowInvalidUserCodeExceptionOnUnknownDomainObjectException() {
        when(projectContext.getVariantContext(any())).thenThrow(UnknownDomainObjectException.class);

        assertThrows(InvalidUserCodeException.class, () -> variantAction.execute(variant));
    }

    @Test
    void prepareConfigurationShouldAddComponentVariants() {
        variantAction.prepareConfiguration(context);

        verify(context.getComponent()).addVariantsFromConfiguration(context.getConfiguration(), context.getMavenMapping());
    }

    @Test
    void prepareConfigurationShouldAddConfigurationArtifact() {
        when(context.getJarProvider().map(context.getPublishArtifact())).thenReturn(provider);

        variantAction.prepareConfiguration(context);

        verify(context.getConfiguration().getArtifacts()).addLater(provider);
    }

    @Test
    void prepareConfigurationShouldSetConfigurationAttributes() {
        variantAction.prepareConfiguration(context);

        verify(context.getConfiguration()).attributes(context.getPublishingAttributes());
    }
}
