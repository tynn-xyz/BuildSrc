//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.api.attributes.ProductFlavorAttr;
import com.android.build.gradle.api.LibraryVariant;
import com.android.builder.model.ProductFlavor;

import org.gradle.api.InvalidUserDataException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.publish.maven.MavenPublication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.gradle.api.attributes.Attribute.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class MavenActionTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    LibraryVariant variant;

    @Mock(answer = RETURNS_DEEP_STUBS)
    ProjectContext context;
    @Mock
    SoftwareComponentFactory factory;

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
    void executeShouldGetReleasePublication() {
        MavenAction action = spy(this.action);
        String variantName = "variantName";
        List<ProductFlavor> productFlavors = singletonList(mock(ProductFlavor.class));
        when(variant.getName()).thenReturn(variantName);
        when(variant.getBuildType().getName()).thenReturn("release");
        when(variant.getProductFlavors()).thenReturn(productFlavors);
        doReturn(null).when(action).getVariantPublication(any(), any());
        doNothing().when(action).configureConfiguration(any(), any(), any(), any());

        action.execute(variant);

        verify(action).getVariantPublication(variantName, productFlavors);
    }

    @Test
    void executeShouldConfigureReleaseConfigurationWithGradle6() {
        MavenAction action = spy(this.action);
        String variantName = "variantName";
        List<ProductFlavor> productFlavors = singletonList(mock(ProductFlavor.class));
        MavenPublication publication = mock(MavenPublication.class);
        when(variant.getName()).thenReturn(variantName);
        when(variant.getBuildType().getName()).thenReturn("release");
        when(variant.getProductFlavors()).thenReturn(productFlavors);
        doReturn(true).when(action).isGradleWithModuleMetaData();
        doReturn(publication).when(action).getVariantPublication(any(), any());
        doNothing().when(action).configureConfiguration(any(), any(), any(), any());

        action.execute(variant);

        verify(action).configureConfiguration(variantName, "release", productFlavors, publication);
    }

    @Test
    void executeShouldNotConfigureReleaseConfigurationWithGradle5() {
        MavenAction action = spy(this.action);
        String variantName = "variantName";
        List<ProductFlavor> productFlavors = singletonList(mock(ProductFlavor.class));
        when(variant.getName()).thenReturn(variantName);
        when(variant.getBuildType().getName()).thenReturn("release");
        when(variant.getProductFlavors()).thenReturn(productFlavors);
        doReturn(false).when(action).isGradleWithModuleMetaData();
        doReturn(mock(MavenPublication.class)).when(action).getVariantPublication(any(), any());

        action.execute(variant);

        verify(action, never()).configureConfiguration(any(), any(), any(), any());
    }

    @Test
    void executeShouldNotConfigureReleaseConfigurationWithoutFlavors() {
        MavenAction action = spy(this.action);
        when(variant.getName()).thenReturn("");
        when(variant.getBuildType().getName()).thenReturn("release");
        when(variant.getProductFlavors()).thenReturn(emptyList());
        doReturn(mock(MavenPublication.class)).when(action).getVariantPublication(any(), any());

        action.execute(variant);

        verify(action, never()).configureConfiguration(any(), any(), any(), any());
        verify(action, never()).isGradleWithModuleMetaData();
    }

    @Test
    void configureAttributesShouldSetAttributes() {
        MavenAction action = spy(this.action);
        AttributeContainer attributes = mock(AttributeContainer.class);
        String name1 = "name1";
        String name2 = "name2";
        String dimension1 = "dimension1";
        String dimension2 = "dimension2";
        ProductFlavor flavor1 = mock(ProductFlavor.class);
        ProductFlavor flavor2 = mock(ProductFlavor.class);
        Attribute<ProductFlavorAttr> attribute1 = mock(Attribute.class);
        Attribute<ProductFlavorAttr> attribute2 = mock(Attribute.class);
        when(flavor1.getName()).thenReturn(name1);
        when(flavor1.getDimension()).thenReturn(dimension1);
        when(flavor2.getName()).thenReturn(name2);
        when(flavor2.getDimension()).thenReturn(dimension2);
        doReturn(attribute1).when(action).getAttribute(dimension1);
        doReturn(attribute2).when(action).getAttribute(dimension2);

        action.configureAttributes(attributes, asList(flavor1, flavor2));

        verify(context).setAttribute(attributes, attribute1, name1);
        verify(context).setAttribute(attributes, attribute2, name2);
    }

    @Test
    void configureComponentShouldAddConfigurationToComponent() {
        MavenAction action = spy(this.action);
        String name = "name";
        Configuration configuration = mock(Configuration.class);
        AdhocComponentWithVariants component = mock(AdhocComponentWithVariants.class);
        when(context.getComponent(name, factory)).thenReturn(component);
        doThrow(InvalidUserDataException.class).when(action).getMavenPublication(name);

        action.configureComponent(name, configuration);

        verify(component).addVariantsFromConfiguration(eq(configuration), any(MavenMapping.class));
    }

    @Test
    void configureComponentShouldConfigurePublication() {
        MavenAction action = spy(this.action);
        String name = "name";
        AdhocComponentWithVariants component = mock(AdhocComponentWithVariants.class);
        MavenPublication publication = mock(MavenPublication.class);
        when(context.getComponent(name, factory)).thenReturn(component);
        doReturn(publication).when(action).getMavenPublication(name);

        action.configureComponent(name, null);

        verify(publication).from(component);
    }

    @Test
    void configureConfigurationShouldCreateConfiguration() {
        MavenAction action = spy(this.action);
        String name = "name";
        String componentName = "componentName";
        List<ProductFlavor> flavors = singletonList(mock(ProductFlavor.class));
        MavenPublication publication = mock(MavenPublication.class);
        doNothing().when(action).configureAttributes(any(), any());
        doNothing().when(action).configureComponent(any(), any());

        action.configureConfiguration(name, componentName, flavors, publication);

        verify(context).getConfiguration("nameMetaPublication");
    }

    @Test
    void configureConfigurationShouldAddDependencyToPublication() {
        MavenAction action = spy(this.action);
        String name = "name";
        String componentName = "componentName";
        List<ProductFlavor> flavors = singletonList(mock(ProductFlavor.class));
        MavenPublication publication = mock(MavenPublication.class);
        Dependency dependency = mock(Dependency.class);
        when(context.getProjectDependency(publication)).thenReturn(dependency);
        doNothing().when(action).configureAttributes(any(), any());
        doNothing().when(action).configureComponent(any(), any());

        action.configureConfiguration(name, componentName, flavors, publication);

        verify(context.getConfiguration(any()).getDependencies()).add(dependency);
    }

    @Test
    void configureConfigurationShouldConfigureAttributes() {
        MavenAction action = spy(this.action);
        String name = "name";
        String componentName = "componentName";
        List<ProductFlavor> flavors = singletonList(mock(ProductFlavor.class));
        MavenPublication publication = mock(MavenPublication.class);
        AttributeContainer attributes = mock(AttributeContainer.class);
        when(context.getConfiguration(any()).getAttributes()).thenReturn(attributes);
        doNothing().when(action).configureAttributes(any(), any());
        doNothing().when(action).configureComponent(any(), any());

        action.configureConfiguration(name, componentName, flavors, publication);

        verify(action).configureAttributes(attributes, flavors);
    }

    @Test
    void configureConfigurationShouldConfigureComponent() {
        MavenAction action = spy(this.action);
        String name = "name";
        String componentName = "componentName";
        List<ProductFlavor> flavors = singletonList(mock(ProductFlavor.class));
        MavenPublication publication = mock(MavenPublication.class);
        Configuration configuration = mock(Configuration.class, RETURNS_DEEP_STUBS);
        when(context.getConfiguration(any())).thenReturn(configuration);
        doNothing().when(action).configureAttributes(any(), any());
        doNothing().when(action).configureComponent(any(), any());

        action.configureConfiguration(name, componentName, flavors, publication);

        verify(action).configureComponent(componentName, configuration);
    }

    @Test
    void setArtifactIdShouldNotSetArtifactIdWithoutFlavors() {
        MavenPublication publication = mock(MavenPublication.class);

        action.setArtifactId(publication, emptyList());

        verifyNoInteractions(publication);
    }

    @Test
    void setArtifactIdShouldSetArtifactIdWithFlavors() {
        String projectName = "projectName";
        String flavor1Name = "flavor1Name";
        String flavor2Name = "flavor2Name";
        ProductFlavor flavor1 = mock(ProductFlavor.class);
        ProductFlavor flavor2 = mock(ProductFlavor.class);
        MavenPublication publication = mock(MavenPublication.class);
        when(flavor1.getName()).thenReturn(flavor1Name);
        when(flavor2.getName()).thenReturn(flavor2Name);
        when(context.getProjectName()).thenReturn(projectName);

        action.setArtifactId(publication, asList(flavor1, flavor2));

        verify(publication).setArtifactId(projectName + '-' + flavor1Name + '-' + flavor2Name);
    }

    @Test
    void getAttributeShouldPrefixNameWithGroup() {
        String name = "name";
        String group = "group";
        when(context.getProjectGroup()).thenReturn(group);

        assertEquals(of(group + '.' + name, ProductFlavorAttr.class), action.getAttribute(name));
    }

    @Test
    void getAttributeShouldNotPrefixNameWithNamespace() {
        String name = "name.space";

        assertEquals(of(name, ProductFlavorAttr.class), action.getAttribute(name));
    }

    @Test
    void getMavenPublicationShouldCreateMavenPublication() {
        String variantName = "variantName";
        MavenPublication publication = mock(MavenPublication.class);
        when(context.getPublishingExtension().getPublications().create(variantName, MavenPublication.class)).thenReturn(publication);

        assertEquals(publication, action.getMavenPublication(variantName));
    }

    @Test
    void getVariantPublicationShouldGetReleasePublication() {
        MavenAction action = spy(this.action);
        String variantName = "variantName";
        List<ProductFlavor> productFlavors = emptyList();
        MavenPublication publication = mock(MavenPublication.class);
        when(action.getMavenPublication(variantName)).thenReturn(publication);

        action.getVariantPublication(variantName, productFlavors);

        verify(publication).from(context.getComponent(variantName));
    }

    @Test
    void getVariantPublicationShouldSetArtifactId() {
        MavenAction action = spy(this.action);
        String variantName = "variantName";
        List<ProductFlavor> productFlavors = singletonList(mock(ProductFlavor.class));
        MavenPublication publication = mock(MavenPublication.class);
        when(action.getMavenPublication(variantName)).thenReturn(publication);
        doNothing().when(action).setArtifactId(any(), any());

        action.getVariantPublication(variantName, productFlavors);

        verify(action).setArtifactId(publication, productFlavors);
    }

    @Test
    void getVariantPublicationShouldNotSetArtifactIdWhenNull() {
        MavenAction action = spy(this.action);
        String variantName = "variantName";
        MavenPublication publication = mock(MavenPublication.class);
        when(action.getMavenPublication(variantName)).thenReturn(publication);
        doNothing().when(action).setArtifactId(any(), any());

        action.getVariantPublication(variantName, emptyList());

        verify(publication, never()).setArtifactId(any());
    }
}
