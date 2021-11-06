//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

import org.gradle.api.Action;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.jvm.tasks.Jar;
import org.jetbrains.dokka.gradle.DokkaTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class TaskContextTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    PublishingScope scope;
    @Mock
    VariantContext variantContext;

    @InjectMocks
    TaskContext context;

    @Test
    void getArtifactAttributesShouldReturnArtifactAttributes() {
        assertTrue(context.getArtifactAttributes() instanceof ArtifactAttributes);
    }

    @Test
    void getArtifactClassifierShouldDelegateToVariantContext() {
        String classifier = "classifier";
        when(variantContext.getArtifactClassifier()).thenReturn(classifier);

        assertEquals(classifier, context.getArtifactClassifier());
    }

    @Test
    void getBootClasspathShouldDelegateToVariantContext() {
        List<File> files = mock(List.class);
        when(variantContext.getBootClasspath()).thenReturn(files);

        assertEquals(files, context.getBootClasspath());
    }

    @Test
    void getComponentShouldDelegateToVariantContext() {
        AdhocComponentWithVariants component = mock(AdhocComponentWithVariants.class);
        when(variantContext.getComponent(scope)).thenReturn(component);

        assertEquals(component, context.getComponent());
    }

    @Test
    void getConfigurationShouldDelegateToVariantContext() {
        Configuration configuration = mock(Configuration.class);
        when(variantContext.getConfiguration(scope)).thenReturn(configuration);

        assertEquals(configuration, context.getConfiguration());
    }

    @Test
    void getDirNameShouldDelegateToScopeAndVariantContext() {
        String name = "name";
        String dirName = "dirName";
        File buildDir = new File("path/to/build/dir");
        when(scope.getName()).thenReturn(name);
        when(variantContext.getBuildDir(name)).thenReturn(buildDir);
        when(variantContext.getDirName()).thenReturn(dirName);

        assertEquals(new File(buildDir, dirName), context.getOutputDirectory());
    }

    @Test
    void getJarProviderShouldDelegateToVariantContext() {
        TaskProvider<Jar> provider = mock(TaskProvider.class);
        when(variantContext.getJarProvider(eq(scope), any(JarTask.class))).thenReturn(provider);

        assertEquals(provider, context.getJarProvider());
    }

    @Test
    void getJavaSourceDirectoriesShouldDelegateToVariantContext() {
        Set<File> directories = singleton(new File("path"));
        when(variantContext.getJavaSourceDirectories()).thenReturn(directories);

        assertEquals(directories, context.getJavaSourceDirectories());
    }

    @Test
    void getJavadocTaskShouldDelegateToVariantContext() {
        TaskProvider<Javadoc> provider = mock(TaskProvider.class);
        when(variantContext.getTaskProvider(eq(scope), eq(Javadoc.class), any(JavadocTask.class))).thenReturn(provider);

        assertEquals(provider, context.getJavadocTask());
    }

    @Test
    void getKdocTaskShouldDelegateToVariantContext() {
        TaskProvider<DokkaTask> provider = mock(TaskProvider.class);
        when(variantContext.getTaskProvider(eq(scope), eq(DokkaTask.class), any(KdocTask.class))).thenReturn(provider);

        assertEquals(provider, context.getKdocTask());
    }

    @Test
    void getMavenMappingShouldReturnMavenMapping() {
        assertTrue(context.getMavenMapping() instanceof MavenMapping);
    }

    @Test
    void getOutputsDirShouldDelegateToVariantContextWithScope() {
        String outputsDir = "outputsDir";
        File buildDir = new File("path/to/build/dir");
        when(scope.getOutputsDir()).thenReturn(outputsDir);
        when(variantContext.getBuildDir(outputsDir)).thenReturn(buildDir);

        assertEquals(buildDir, context.getOutputsDir());
    }

    @Test
    void getPublishArtifactShouldDelegateToVariantContext() {
        Transformer<PublishArtifact, AbstractArchiveTask> transformer = mock(Transformer.class);
        when(variantContext.getVariantArtifact(scope)).thenReturn(transformer);

        assertEquals(transformer, context.getPublishArtifact());
    }

    @Test
    void getPublishingAttributesShouldDelegateToScopeFactory() {
        Action<AttributeContainer> action = mock(Action.class);
        when(scope.getPublishingAttributes().apply(context)).thenReturn(action);

        assertEquals(action, context.getPublishingAttributes());
    }

    @Test
    void getSourceDirectoriesShouldContainJavaAndKotlinDirectories() {
        File java = new File("java");
        File kotlin = new File("kotlin");
        when(variantContext.getJavaSourceDirectories()).thenReturn(singleton(java));
        when(variantContext.getKotlinSourceDirectories()).thenReturn(singleton(kotlin));

        assertEquals(new HashSet<>(asList(java, kotlin)), context.getSourceDirectories());
    }

    @Test
    void getVariantAttributesShouldReturnVariantAttributes() {
        assertTrue(context.getVariantAttributes() instanceof VariantAttributes);
    }

    @Test
    void containsSourceSetShouldDelegateToVariantContext() {
        String name = "name";
        boolean contains = new Random().nextBoolean();
        when(variantContext.containsSourceSet(name)).thenReturn(contains);

        assertEquals(contains, context.containsSourceSet(name));
        verify(variantContext).containsSourceSet(name);
    }
}
