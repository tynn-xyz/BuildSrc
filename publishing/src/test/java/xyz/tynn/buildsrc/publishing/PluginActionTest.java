//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PluginActionTest {

    @Mock
    LibraryPlugin plugin;

    @Mock(answer = RETURNS_DEEP_STUBS)
    ProjectContext context;
    @Mock
    Action<LibraryVariant> variantAction;

    @InjectMocks
    PluginAction action;

    @Test
    void executeShouldRunVariantActionOnAllVariants() {
        action.execute(plugin);

        verify(context.getLibraryExtension().getLibraryVariants()).all(variantAction);
    }

    @Test
    void executeShouldNotInteractWithPlugin() {
        action.execute(plugin);

        verifyNoInteractions(plugin);
    }
}
