//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.attributes.DocsType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.android.builder.model.AndroidProject.FD_OUTPUTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static xyz.tynn.buildsrc.publishing.ArtifactScope.JAVADOC;
import static xyz.tynn.buildsrc.publishing.ArtifactScope.SOURCES;
import static xyz.tynn.buildsrc.publishing.MavenScope.COMPILE;
import static xyz.tynn.buildsrc.publishing.MavenScope.RUNTIME;

@ExtendWith(MockitoExtension.class)
class ArtifactScopeTest {

    @Mock
    TaskContext context;

    @Test
    void javadocNameShouldBeJavadocDocsType() {
        assertEquals(DocsType.JAVADOC, JAVADOC.getName());
    }

    @Test
    void javadocMavenScopeShouldBeCOMPILE() {
        assertEquals(COMPILE, JAVADOC.getMavenScope());
    }

    @Test
    void javadocJarConfigShouldBeJavadocJarFactory() {
        JAVADOC.getSourcePath().apply(context);

        verify(context).getJavadocTask();
    }

    @Test
    void javadocOutputsDirShouldBeUnderOutputs() {
        assertEquals(FD_OUTPUTS + '/' + DocsType.JAVADOC, JAVADOC.getOutputsDir());
    }

    @Test
    void sourcesNameShouldBeSourcesDocsType() {
        assertEquals(DocsType.SOURCES, SOURCES.getName());
    }

    @Test
    void sourcesMavenScopeShouldBeRuntime() {
        assertEquals(RUNTIME, SOURCES.getMavenScope());
    }

    @Test
    void sourcesJarConfigShouldBeSourcesJarFactory() {
        SOURCES.getSourcePath().apply(context);

        verify(context).getSourceDirectories();
    }

    @Test
    void sourcesOutputsDirShouldBeUnderOutputs() {
        assertEquals(FD_OUTPUTS + '/' + DocsType.SOURCES, SOURCES.getOutputsDir());
    }
}
