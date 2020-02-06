//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.attributes.DocsType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.gradle.api.attributes.Usage.JAVA_API;
import static org.gradle.api.attributes.Usage.JAVA_RUNTIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static xyz.tynn.buildsrc.publishing.ArtifactScope.JAVADOC;
import static xyz.tynn.buildsrc.publishing.ArtifactScope.SOURCES;
import static xyz.tynn.buildsrc.publishing.MavenScope.COMPILE;
import static xyz.tynn.buildsrc.publishing.MavenScope.RUNTIME;
import static xyz.tynn.buildsrc.publishing.PublishingScope.ALL_VARIANT_JAVADOC;
import static xyz.tynn.buildsrc.publishing.PublishingScope.ALL_VARIANT_SOURCES;
import static xyz.tynn.buildsrc.publishing.PublishingScope.VARIANT_JAVADOC;
import static xyz.tynn.buildsrc.publishing.PublishingScope.VARIANT_SOURCES;

@SuppressWarnings({"ResultOfMethodCallIgnored", "WeakerAccess"})
@ExtendWith(MockitoExtension.class)
class PublishingScopeTest {

    String variantName = "variantName";

    @Mock
    TaskContext taskContext;

    // ALL_VARIANT_JAVADOC

    @Test
    void allVariantJavadocArtifactClassifierShouldHaveVariantNamePrefix() {
        assertEquals(variantName + "-javadoc", ALL_VARIANT_JAVADOC.getArtifactClassifier(variantName));
    }

    @Test
    void allVariantJavadocComponentNameShouldBeAll() {
        assertEquals("all", ALL_VARIANT_JAVADOC.getComponentName(variantName));
    }

    @Test
    void allVariantJavadocDocsTypeShouldBeJavadoc() {
        assertEquals(DocsType.JAVADOC, ALL_VARIANT_JAVADOC.getDocsType());
    }

    @Test
    void allVariantJavadocNameShouldBeVariantJavadoc() {
        assertEquals(JAVADOC.getName(), ALL_VARIANT_JAVADOC.getName());
    }

    @Test
    void allVariantJavadocOutputsDirShouldBeJavadoc() {
        assertEquals(JAVADOC.getOutputsDir(), ALL_VARIANT_JAVADOC.getOutputsDir());
    }

    @Test
    void allVariantJavadocSourcePathShouldBeJavadocJar() {
        ALL_VARIANT_JAVADOC.getSourcePath().apply(taskContext);

        verify(taskContext).getJavadocTask();
    }

    @Test
    void allVariantJavadocMavenScopeShouldBeCompile() {
        assertEquals(COMPILE.getName(), ALL_VARIANT_JAVADOC.getMavenScope());
    }

    @Test
    void allVariantJavadocPublishingAttributesShouldBeVariantAttributes() {
        ALL_VARIANT_JAVADOC.getPublishingAttributes().apply(taskContext);

        verify(taskContext).getVariantAttributes();
    }

    @Test
    void allVariantJavadocPublicationNameShouldBeAllVariantJavadoc() {
        assertEquals(variantName + "AllJavadocPublication", ALL_VARIANT_JAVADOC.getPublicationName(variantName));
    }

    @Test
    void allVariantJavadocUsageShouldBeJavaApi() {
        assertEquals(JAVA_API, ALL_VARIANT_JAVADOC.getUsage());
    }

    // VARIANT_JAVADOC

    @Test
    void variantJavadocArtifactClassifierShouldBeJavadoc() {
        assertEquals(JAVADOC.getName(), VARIANT_JAVADOC.getArtifactClassifier(variantName));
    }

    @Test
    void variantJavadocComponentNameShouldBeVariantName() {
        assertEquals(variantName, VARIANT_JAVADOC.getComponentName(variantName));
    }

    @Test
    void variantJavadocDocsTypeShouldBeJavadoc() {
        assertEquals(DocsType.JAVADOC, VARIANT_JAVADOC.getDocsType());
    }

    @Test
    void variantJavadocNameShouldBeVariantJavadoc() {
        assertEquals(JAVADOC.getName(), VARIANT_JAVADOC.getName());
    }

    @Test
    void variantJavadocOutputsDirShouldBeJavadoc() {
        assertEquals(JAVADOC.getOutputsDir(), VARIANT_JAVADOC.getOutputsDir());
    }

    @Test
    void variantJavadocSourcePathShouldBeJavadocJar() {
        VARIANT_JAVADOC.getSourcePath().apply(taskContext);

        verify(taskContext).getJavadocTask();
    }

    @Test
    void variantJavadocMavenScopeShouldBeCompile() {
        assertEquals(COMPILE.getName(), VARIANT_JAVADOC.getMavenScope());
    }

    @Test
    void variantJavadocPublishingAttributesShouldBeArtifactAttributes() {
        VARIANT_JAVADOC.getPublishingAttributes().apply(taskContext);

        verify(taskContext).getArtifactAttributes();
    }

    @Test
    void variantJavadocPublicationNameShouldBeAllVariantJavadoc() {
        assertEquals(variantName + "JavadocPublication", VARIANT_JAVADOC.getPublicationName(variantName));
    }

    @Test
    void variantJavadocUsageShouldBeJavaApi() {
        assertEquals(JAVA_API, VARIANT_JAVADOC.getUsage());
    }

    // ALL_VARIANT_SOURCES

    @Test
    void allVariantSourcesArtifactClassifierShouldHaveVariantNamePrefix() {
        assertEquals(variantName + "-sources", ALL_VARIANT_SOURCES.getArtifactClassifier(variantName));
    }

    @Test
    void allVariantSourcesComponentNameShouldBeAll() {
        assertEquals("all", ALL_VARIANT_SOURCES.getComponentName(variantName));
    }

    @Test
    void allVariantSourcesDocsTypeShouldBeSources() {
        assertEquals(DocsType.SOURCES, ALL_VARIANT_SOURCES.getDocsType());
    }

    @Test
    void allVariantSourcesNameShouldBeVariantSources() {
        assertEquals(SOURCES.getName(), ALL_VARIANT_SOURCES.getName());
    }

    @Test
    void allVariantSourcesOutputsDirShouldBeSources() {
        assertEquals(SOURCES.getOutputsDir(), ALL_VARIANT_SOURCES.getOutputsDir());
    }

    @Test
    void allVariantSourcesSourcePathShouldBeSourcesJar() {
        ALL_VARIANT_SOURCES.getSourcePath().apply(taskContext);

        verify(taskContext).getSourceDirectories();
    }

    @Test
    void allVariantSourcesMavenScopeShouldBeRuntime() {
        assertEquals(RUNTIME.getName(), ALL_VARIANT_SOURCES.getMavenScope());
    }

    @Test
    void allVariantSourcesPublishingAttributesShouldBeVariantAttributes() {
        ALL_VARIANT_SOURCES.getPublishingAttributes().apply(taskContext);

        verify(taskContext).getVariantAttributes();
    }

    @Test
    void allVariantSourcesPublicationNameShouldBeAllVariantSources() {
        assertEquals(variantName + "AllSourcesPublication", ALL_VARIANT_SOURCES.getPublicationName(variantName));
    }

    @Test
    void allVariantSourcesUsageShouldBeJavaRuntime() {
        assertEquals(JAVA_RUNTIME, ALL_VARIANT_SOURCES.getUsage());
    }

    // VARIANT_SOURCES

    @Test
    void variantSourcesArtifactClassifierShouldBeSources() {
        assertEquals(SOURCES.getName(), VARIANT_SOURCES.getArtifactClassifier(variantName));
    }

    @Test
    void variantSourcesComponentNameShouldBeVariantName() {
        assertEquals(variantName, VARIANT_SOURCES.getComponentName(variantName));
    }

    @Test
    void variantSourcesDocsTypeShouldBeSources() {
        assertEquals(DocsType.SOURCES, VARIANT_SOURCES.getDocsType());
    }

    @Test
    void variantSourcesNameShouldBeVariantSources() {
        assertEquals(SOURCES.getName(), VARIANT_SOURCES.getName());
    }

    @Test
    void variantSourcesOutputsDirShouldBeSources() {
        assertEquals(SOURCES.getOutputsDir(), VARIANT_SOURCES.getOutputsDir());
    }

    @Test
    void variantSourcesSourcePathShouldBeSourcesJar() {
        VARIANT_SOURCES.getSourcePath().apply(taskContext);

        verify(taskContext).getSourceDirectories();
    }

    @Test
    void variantSourcesMavenScopeShouldBeRuntime() {
        assertEquals(RUNTIME.getName(), VARIANT_SOURCES.getMavenScope());
    }

    @Test
    void variantSourcesPublishingAttributesShouldBeArtifactAttributes() {
        VARIANT_SOURCES.getPublishingAttributes().apply(taskContext);

        verify(taskContext).getArtifactAttributes();
    }

    @Test
    void variantSourcesPublicationNameShouldBeAllVariantSources() {
        assertEquals(variantName + "SourcesPublication", VARIANT_SOURCES.getPublicationName(variantName));
    }

    @Test
    void variantSourcesUsageShouldBeJavaRuntime() {
        assertEquals(JAVA_RUNTIME, VARIANT_SOURCES.getUsage());
    }
}
