//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.api.LibraryVariant;
import com.android.builder.model.ProductFlavor;

import org.gradle.api.publish.maven.MavenPublication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MavenActionTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    LibraryVariant variant;

    @Mock(answer = RETURNS_DEEP_STUBS)
    ProjectContext context;

    @InjectMocks
    MavenAction action;

    @Test
    void executeShouldSkipNonRelease() {
        when(variant.getBuildType().getName()).thenReturn("debug");

        action.execute(variant);

        verify(variant, atLeastOnce()).getBuildType();
        verifyNoMoreInteractions(variant);
        verifyNoInteractions(context);
    }

    @Test
    void executeShouldCreateReleasePublication() {
        MavenAction action = spy(this.action);
        String variantName = "variantName";
        String artifactId = "artifactId";
        List<ProductFlavor> productFlavors = emptyList();
        MavenPublication publication = mock(MavenPublication.class);
        when(variant.getName()).thenReturn(variantName);
        when(variant.getBuildType().getName()).thenReturn("release");
        when(variant.getProductFlavors()).thenReturn(productFlavors);
        when(action.getArtifactId(productFlavors)).thenReturn(artifactId);
        when(action.getMavenPublication(variantName)).thenReturn(publication);

        action.execute(variant);

        verify(publication).from(context.getComponent(variantName));
        verify(publication).setArtifactId(artifactId);
    }

    @Test
    void getArtifactIdShouldReturnProjectNameWithoutFlavors() {
        String projectName = "projectName";
        when(context.getProjectName()).thenReturn(projectName);

        assertEquals(projectName, action.getArtifactId(emptyList()));
    }

    @Test
    void getArtifactIdShouldReturnJoinedProjectNameWithFlavors() {
        String projectName = "projectName";
        String flavor1Name = "flavor1Name";
        String flavor2Name = "flavor2Name";
        ProductFlavor flavor1 = mock(ProductFlavor.class);
        ProductFlavor flavor2 = mock(ProductFlavor.class);
        when(flavor1.getName()).thenReturn(flavor1Name);
        when(flavor2.getName()).thenReturn(flavor2Name);
        when(context.getProjectName()).thenReturn(projectName);

        assertEquals(projectName + '-' + flavor1Name + '-' + flavor2Name, action.getArtifactId(asList(flavor1, flavor2)));
    }

    @Test
    void getMavenPublicationShouldCreateMavenPublication() {
        String variantName = "variantName";
        MavenPublication publication = mock(MavenPublication.class);
        when(context.getPublishingExtension().getPublications().create(variantName, MavenPublication.class)).thenReturn(publication);

        assertEquals(publication, action.getMavenPublication(variantName));
    }
}
