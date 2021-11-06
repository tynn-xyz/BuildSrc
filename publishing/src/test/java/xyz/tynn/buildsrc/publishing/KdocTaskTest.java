//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.gradle.api.Action;
import org.jetbrains.dokka.gradle.DokkaTask;
import org.jetbrains.dokka.gradle.GradleDokkaSourceSetBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

@ExtendWith(MockitoExtension.class)
class KdocTaskTest {

    @Captor
    ArgumentCaptor<Action<GradleDokkaSourceSetBuilder>> action;

    @Mock(answer = RETURNS_DEEP_STUBS)
    DokkaTask dokka;

    @Mock(answer = RETURNS_DEEP_STUBS)
    GradleDokkaSourceSetBuilder builder;

    @Mock(answer = RETURNS_DEEP_STUBS)
    TaskContext context;

    @InjectMocks
    KdocTask task;

    @Test
    void executeShouldKeepVariantSourceSets() {
        when(context.getOutputDirectory()).thenReturn(new File(""));
        when(context.containsSourceSet(any())).thenReturn(true);

        task.execute(dokka);

        verify(dokka.getDokkaSourceSets()).all(action.capture());
        action.getValue().execute(builder);
        verify(builder.getSuppress(), never()).set(true);
    }

    @Test
    void executeShouldSuppressNonVariantSourceSets() {
        when(context.getOutputDirectory()).thenReturn(new File(""));
        when(context.containsSourceSet(any())).thenReturn(false);

        task.execute(dokka);

        verify(dokka.getDokkaSourceSets()).all(action.capture());
        action.getValue().execute(builder);
        verify(builder.getSuppress()).set(true);
    }

    @Test
    void executeShouldSetOutputDirectory() {
        File outputDirectory = new File("outputDirectory");
        when(context.getOutputDirectory()).thenReturn(outputDirectory);

        task.execute(dokka);

        verify(dokka.getOutputDirectory()).set(outputDirectory);
    }
}
