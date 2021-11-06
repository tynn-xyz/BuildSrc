//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

@ExtendWith(MockitoExtension.class)
class JavadocTaskTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    Javadoc javadoc;

    @Mock(answer = RETURNS_DEEP_STUBS)
    TaskContext context;

    @InjectMocks
    JavadocTask task;

    @Test
    void executeShouldAddBootClasspath() {
        when(context.getOutputDirectory()).thenReturn(new File(""));

        task.execute(javadoc);

        verify(javadoc.getClasspath()).plus(javadoc.getProject().files(context.getBootClasspath()));
    }

    @Test
    void executeShouldLinkAndroidDocumentation() {
        StandardJavadocDocletOptions options = mock(StandardJavadocDocletOptions.class);
        when(javadoc.getOptions()).thenReturn(options);
        when(context.getOutputDirectory()).thenReturn(new File(""));

        task.execute(javadoc);

        verify(options).links("https://developer.android.com/reference/");
    }

    @Test
    void executeShouldSetDestinationDir() {
        File outputDirectory = new File("buildDir");
        when(context.getOutputDirectory()).thenReturn(outputDirectory);

        task.execute(javadoc);

        verify(javadoc).setDestinationDir(outputDirectory);
    }

    @Test
    void executeShouldSetSource() {
        when(context.getOutputDirectory()).thenReturn(new File(""));

        task.execute(javadoc);

        verify(javadoc).setSource(context.getJavaSourceDirectories());
    }
}
