//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.gradle.jvm.tasks.Jar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

@ExtendWith(MockitoExtension.class)
class JarTaskTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    Jar jar;

    @Mock(answer = RETURNS_DEEP_STUBS)
    TaskContext context;

    @InjectMocks
    JarTask task;

    @Test
    void executeShouldSetArchiveClassifier() {
        String classifier = "classifier";
        when(context.getArtifactClassifier()).thenReturn(classifier);
        when(jar.getProject().getBuildDir()).thenReturn(new File(""));
        when(context.getOutputsDir()).thenReturn(new File(""));

        task.execute(jar);

        verify(jar.getArchiveClassifier()).set(classifier);
    }

    @Test
    void executeShouldSetDestinationDirectory() {
        File outputsDir = new File("buildDir");
        when(context.getOutputsDir()).thenReturn(outputsDir);

        task.execute(jar);

        verify(jar.getDestinationDirectory()).set(outputsDir);
    }

    @Test
    void executeShouldSetFromVariantSources() {
        when(jar.getProject().getBuildDir()).thenReturn(new File(""));
        when(context.getOutputsDir()).thenReturn(new File(""));

        task.execute(jar);

        verify(jar).from(context.getSourcePath());
    }
}
