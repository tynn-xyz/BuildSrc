//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.jetbrains.dokka.gradle.DokkaTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static java.util.Collections.singletonList;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KdocTaskTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    DokkaTask dokka;

    @Mock(answer = RETURNS_DEEP_STUBS)
    TaskContext context;

    @InjectMocks
    KdocTask task;

    @Test
    void executeShouldSetAndroidVariants() {
        String name = "name";
        when(context.getVariantName()).thenReturn(name);
        when(context.getDirName()).thenReturn("");
        when(dokka.getProject().getBuildDir()).thenReturn(new File(""));

        task.execute(dokka);

        verify(dokka.getConfiguration()).setAndroidVariants(singletonList(name));
    }

    @Test
    void executeShouldSetOutputFormat() {
        when(context.getDirName()).thenReturn("");
        when(dokka.getProject().getBuildDir()).thenReturn(new File(""));

        task.execute(dokka);

        verify(dokka).setOutputFormat("html");
    }

    @Test
    void executeShouldSetOutputDirectory() {
        String dirName = "dirName";
        File buildDir = new File("buildDir");
        when(context.getDirName()).thenReturn(dirName);
        when(dokka.getProject().getBuildDir()).thenReturn(buildDir);

        task.execute(dokka);

        verify(dokka).setOutputDirectory(new File(buildDir, dirName).getAbsolutePath());
    }
}
