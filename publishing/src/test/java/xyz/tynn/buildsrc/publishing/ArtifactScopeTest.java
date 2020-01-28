//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Action;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.jvm.tasks.Jar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static xyz.tynn.buildsrc.publishing.ArtifactScope.ALL_VARIANT_SOURCES;
import static xyz.tynn.buildsrc.publishing.ArtifactScope.VARIANT_SOURCES;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"SuspiciousMethodCalls", "WeakerAccess"})
class ArtifactScopeTest {

    String variantName = "variantName";

    @Mock
    VariantContext context;
    @Mock
    Action<?> action;

    @Test
    void allVariantSourcesArtifactAttributesShouldNotContainUnknownActions() {
        List<Action<AttributeContainer>> attributes = ALL_VARIANT_SOURCES.getArtifactAttributes(context);

        assertEquals(3, attributes.size());
    }

    @Test
    void allVariantSourcesArtifactAttributesShouldContainSourcesAttributes() {
        doReturn(action).when(context).getSourcesAttributes();

        List<Action<AttributeContainer>> attributes = ALL_VARIANT_SOURCES.getArtifactAttributes(context);

        assertTrue(attributes.contains(action));
        verify(context).getSourcesAttributes();
    }

    @Test
    void allVariantSourcesArtifactAttributesShouldContainFlavorAttributes() {
        doReturn(action).when(context).getFlavorAttributes();

        List<Action<AttributeContainer>> attributes = ALL_VARIANT_SOURCES.getArtifactAttributes(context);

        assertTrue(attributes.contains(action));
        verify(context).getFlavorAttributes();
    }

    @Test
    void allVariantSourcesArtifactAttributesShouldContainVariantAttributes() {
        doReturn(action).when(context).getVariantAttributes();

        List<Action<AttributeContainer>> attributes = ALL_VARIANT_SOURCES.getArtifactAttributes(context);

        assertTrue(attributes.contains(action));
        verify(context).getVariantAttributes();
    }

    @Test
    void allVariantSourcesArtifactClassifierShouldHaveVariantNamePrefix() {
        String classifier = ALL_VARIANT_SOURCES.getArtifactClassifier(variantName);

        assertEquals(variantName + "-sources", classifier);
    }

    @Test
    void allVariantSourcesComponentNameShouldBeAll() {
        String componentName = ALL_VARIANT_SOURCES.getComponentName(variantName);

        assertEquals("all", componentName);
    }

    @Test
    void allVariantSourcesJarConfigShouldDelegateToContext() {
        doReturn(action).when(context).getSourcesJar(any());

        Action<Jar> jarConfig = ALL_VARIANT_SOURCES.getJarConfig(context);

        assertEquals(action, jarConfig);
        verify(context).getSourcesJar("sources");
    }

    @Test
    void allVariantSourcesJarNameShouldContainSources() {
        String jarName = ALL_VARIANT_SOURCES.getJarName(variantName);

        assertEquals(variantName + "SourcesJar", jarName);
    }

    @Test
    void allVariantSourcesPublicationNameShouldContainAll() {
        String publicationName = ALL_VARIANT_SOURCES.getPublicationName(variantName);

        assertEquals(variantName + "AllSourcesPublication", publicationName);
    }

    @Test
    void variantSourcesArtifactAttributesShouldContainSourcesAttributes() {
        doReturn(action).when(context).getSourcesAttributes();

        List<Action<AttributeContainer>> attributes = VARIANT_SOURCES.getArtifactAttributes(context);

        assertEquals(singletonList(action), attributes);
        verify(context).getSourcesAttributes();
    }

    @Test
    void variantSourcesArtifactClassifierShouldBeSources() {
        String classifier = VARIANT_SOURCES.getArtifactClassifier(variantName);

        assertEquals("sources", classifier);
    }

    @Test
    void variantSourcesComponentNameShouldBeTheVariantName() {
        String componentName = VARIANT_SOURCES.getComponentName(variantName);

        assertEquals(variantName, componentName);
    }

    @Test
    void variantSourcesJarConfigShouldDelegateToContext() {
        doReturn(action).when(context).getSourcesJar(any());

        Action<Jar> jarConfig = VARIANT_SOURCES.getJarConfig(context);

        assertEquals(action, jarConfig);
        verify(context).getSourcesJar("sources");
    }

    @Test
    void variantSourcesJarNameShouldContainSources() {
        String jarName = VARIANT_SOURCES.getJarName(variantName);

        assertEquals(variantName + "SourcesJar", jarName);
    }

    @Test
    void variantSourcesPublicationNameShouldNotContainAll() {
        String publicationName = VARIANT_SOURCES.getPublicationName(variantName);

        assertEquals(variantName + "SourcesPublication", publicationName);
    }
}
