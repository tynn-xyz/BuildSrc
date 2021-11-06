//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import static org.gradle.api.attributes.Attribute.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

import com.android.build.api.attributes.BuildTypeAttr;
import com.android.build.api.attributes.ProductFlavorAttr;
import com.android.build.gradle.api.LibraryVariant;
import com.android.builder.model.ProductFlavor;
import com.android.builder.model.SourceProvider;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.internal.HasConvention;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class VariantContextTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    ProjectContext projectContext;
    @Mock(answer = RETURNS_DEEP_STUBS)
    LibraryVariant variant;

    @Mock(answer = RETURNS_DEEP_STUBS)
    PublishingScope scope;

    @InjectMocks
    VariantContext context;

    @Test
    void setAttributeShouldDelegateToProjectContext() {
        String name = "name";
        Attribute<Named> key = of(Named.class);
        AttributeContainer attributes = mock(AttributeContainer.class);

        context.setAttribute(attributes, key, name);

        verify(projectContext).setAttribute(attributes, key, name);
    }

    @Test
    void setBuildTypeAttributeShouldSetBuildTypeAttr() {
        String name = "name";
        AttributeContainer attributes = mock(AttributeContainer.class);
        when(variant.getBuildType().getName()).thenReturn(name);

        context.setBuildTypeAttribute(attributes);

        verify(projectContext).setAttribute(attributes, BuildTypeAttr.ATTRIBUTE, name);
    }

    @Test
    void setProductFlavorAttributesShouldSetProductFlavorAttr() {
        String name = "name";
        String dimension = "dimension";
        ProductFlavor flavor = mock(ProductFlavor.class);
        when(flavor.getName()).thenReturn(name);
        when(flavor.getDimension()).thenReturn(dimension);
        AttributeContainer attributes = mock(AttributeContainer.class);
        Attribute<ProductFlavorAttr> attr = of(dimension, ProductFlavorAttr.class);
        when(variant.getProductFlavors()).thenReturn(singletonList(flavor));

        context.setProductFlavorAttributes(attributes);

        verify(projectContext).setAttribute(attributes, attr, name);
    }

    @Test
    void getArtifactClassifierShouldConcatFlavorsWithBuildType() {
        String buildTypeName = "buildType";
        String flavor1Name = "flavor1";
        String flavor2Name = "flavor2";
        ProductFlavor flavor1 = mock(ProductFlavor.class);
        ProductFlavor flavor2 = mock(ProductFlavor.class);
        when(flavor1.getName()).thenReturn(flavor1Name);
        when(flavor2.getName()).thenReturn(flavor2Name);
        when(variant.getProductFlavors()).thenReturn(asList(flavor1, flavor2));
        when(variant.getBuildType().getName()).thenReturn(buildTypeName);

        assertEquals(flavor1Name + '-' + flavor2Name + '-' + buildTypeName, context.getArtifactClassifier());
    }

    @Test
    void getBootClasspathShouldReturnBootClasspathFromExtension() {
        List<File> files = mock(List.class);
        when(projectContext.getLibraryExtension().getBootClasspath()).thenReturn(files);

        assertEquals(files, context.getBootClasspath());
    }

    @Test
    void getBuildDirShouldDelegateToProjectContext() {
        String child = "child";
        File buildDir = new File("path/to/build/dir");
        when(projectContext.getBuildDir()).thenReturn(buildDir);

        assertEquals(new File(buildDir, child), context.getBuildDir(child));
    }

    @Test
    void getComponentShouldDelegateToProjectContext() {
        String name = "name";
        String componentName = "componentName";
        AdhocComponentWithVariants component = mock(AdhocComponentWithVariants.class);
        when(projectContext.getComponent(componentName)).thenReturn(component);
        when(scope.getComponentName(name)).thenReturn(componentName);
        when(variant.getName()).thenReturn(name);

        assertEquals(component, context.getComponent(scope));
    }

    @Test
    void getConfigurationShouldDelegateToProjectContext() {
        String name = "name";
        String configurationName = "configurationName";
        Configuration configuration = mock(Configuration.class);
        when(projectContext.getConfiguration(configurationName)).thenReturn(configuration);
        when(scope.getPublicationName(name)).thenReturn(configurationName);
        when(variant.getName()).thenReturn(name);

        assertEquals(configuration, context.getConfiguration(scope));
    }

    @Test
    void getDirNameShouldGetDirNameFromVariant() {
        String name = "name";
        when(variant.getDirName()).thenReturn(name);

        assertEquals(name, context.getDirName());
    }

    @Test
    void getJavaSourceDirectoriesShouldContainsAllJavaSources() {
        Set<File> sourceDirectories = singleton(new File("java"));
        SourceProvider sourceProvider = mock(SourceProvider.class);
        when(variant.getSourceSets()).thenReturn(singletonList(sourceProvider));
        when(sourceProvider.getJavaDirectories()).thenReturn(sourceDirectories);

        assertEquals(sourceDirectories, context.getJavaSourceDirectories());
    }

    @Test
    void getKotlinSourceDirectoriesShouldContainsAllKotlinSources() {
        Set<File> sourceDirectories = singleton(new File("kotlin"));
        MockSettings hasConventionSettings = withSettings()
                .defaultAnswer(RETURNS_DEEP_STUBS)
                .extraInterfaces(SourceProvider.class);
        HasConvention hasConvention = mock(HasConvention.class, hasConventionSettings);
        KotlinSourceSet sourceSet = mock(KotlinSourceSet.class, RETURNS_DEEP_STUBS);
        when(variant.getSourceSets()).thenReturn(singletonList((SourceProvider) hasConvention));
        when(hasConvention.getConvention().getPlugin(KotlinSourceSet.class)).thenReturn(sourceSet);
        when(sourceSet.getKotlin().getSrcDirs()).thenReturn(sourceDirectories);

        assertEquals(sourceDirectories, context.getKotlinSourceDirectories());
    }

    @Test
    void getKotlinSourceDirectoriesShouldBeEmptyWithoutKotlin() {
        SourceProvider sourceProvider = mock(SourceProvider.class);
        when(variant.getSourceSets()).thenReturn(singletonList(sourceProvider));

        assertEquals(emptySet(), context.getKotlinSourceDirectories());
    }

    @Test
    void getTaskContextShouldReturnTaskContext() {
        assertTrue(context.getTaskContext(scope) instanceof TaskContext);
    }

    @Test
    void getJarProviderShouldAddBuildDependsOn() {
        TaskProvider<Jar> provider = mock(TaskProvider.class);
        when(variant.getName()).thenReturn("");
        when(scope.getName()).thenReturn("");
        when(projectContext.getTaskProvider(any(), eq(Jar.class), any())).thenReturn(provider);

        context.getJarProvider(scope, null);

        verify(projectContext).getTaskProvider("build", provider);
    }

    @Test
    void getJarProviderShouldDelegateToProjectContext() {
        String variantName = "variantName";
        String scopeName = "scopeName";
        Action<Jar> config = mock(Action.class);
        when(variant.getName()).thenReturn(variantName);
        when(scope.getName()).thenReturn(scopeName);

        context.getJarProvider(scope, config);

        verify(projectContext).getTaskProvider(ZeroUtils.joinCapitalized(variantName, scopeName, "jar"), Jar.class, config);
    }

    @Test
    void getTaskProviderShouldDelegateToProjectContext() {
        String variantName = "variantName";
        String scopeName = "scopeName";
        Class<Task> type = Task.class;
        Action<Task> config = mock(Action.class);
        when(variant.getName()).thenReturn(variantName);
        when(scope.getName()).thenReturn(scopeName);

        context.getTaskProvider(scope, type, config);

        verify(projectContext).getTaskProvider(ZeroUtils.joinCapitalized(variantName, scopeName), type, config);
    }

    @Test
    void getVariantArtifactShouldReturnVariantArtifact() {
        assertTrue(context.getVariantArtifact(scope) instanceof VariantArtifact);
    }

    @Test
    void connectAssembleTasksShouldAddBuildConfigDependsOn() {
        TaskProvider<?> provider = mock(TaskProvider.class);
        ProductFlavor productFlavor = mock(ProductFlavor.class);
        when(productFlavor.getName()).thenReturn("productFlavor");
        when(variant.getProductFlavors()).thenReturn(singletonList(productFlavor));
        when(variant.getBuildType().getName()).thenReturn("builtType");
        when(variant.getFlavorName()).thenReturn("flavor");

        context.connectAssembleTasks(provider);

        verify(projectContext).getTaskProvider("assembleBuiltType", provider);
        verify(projectContext).getTaskProvider("assembleFlavor", provider);
        verify(projectContext).getTaskProvider("assembleProductFlavor", provider);
    }

    @Test
    void connectAssembleTasksShouldAddFlavorDependsOn() {
        TaskProvider<?> provider = mock(TaskProvider.class);
        when(variant.getBuildType().getName()).thenReturn("builtType");
        when(variant.getFlavorName()).thenReturn("flavor");

        context.connectAssembleTasks(provider);

        verify(projectContext).getTaskProvider("assembleFlavor", provider);
    }

    @Test
    void containsSourceSetShouldReturnTrueWhenInSourceSets() {
        String name = "name";
        SourceProvider provider = mock(SourceProvider.class);
        when(provider.getName()).thenReturn(name);
        when(variant.getSourceSets()).thenReturn(singletonList(provider));

        assertTrue(context.containsSourceSet(name));
    }

    @Test
    void containsSourceSetShouldReturnFalseWhenNotInSourceSets() {
        String name = "name";
        SourceProvider provider = mock(SourceProvider.class);
        when(provider.getName()).thenReturn("not" + name);
        when(variant.getSourceSets()).thenReturn(singletonList(provider));

        assertFalse(context.containsSourceSet(name));
    }
}
