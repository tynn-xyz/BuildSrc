//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(javadoc.getProject().getBuildDir()).thenReturn(new File(""));
        when(context.getDirName()).thenReturn("");

        task.execute(javadoc);

        verify(javadoc.getClasspath()).plus(javadoc.getProject().files(context.getBootClasspath()));
    }

    @Test
    void executeShouldLinkAndroidDocumentation() {
        StandardJavadocDocletOptions options = mock(StandardJavadocDocletOptions.class);
        when(javadoc.getOptions()).thenReturn(options);
        when(javadoc.getProject().getBuildDir()).thenReturn(new File(""));
        when(context.getDirName()).thenReturn("");

        task.execute(javadoc);

        verify(options).links("https://developer.android.com/reference/");
    }

    @Test
    void executeShouldSetDestinationDir() {
        String dirName = "dirName";
        File buildDir = new File("buildDir");
        when(javadoc.getProject().getBuildDir()).thenReturn(buildDir);
        when(context.getDirName()).thenReturn(dirName);

        task.execute(javadoc);

        verify(javadoc).setDestinationDir(new File(buildDir, dirName));
    }

    @Test
    void executeShouldSetSource() {
        when(javadoc.getProject().getBuildDir()).thenReturn(new File(""));
        when(context.getDirName()).thenReturn("");

        task.execute(javadoc);

        verify(javadoc).setSource(context.getJavaSourceDirectories());
    }
}
