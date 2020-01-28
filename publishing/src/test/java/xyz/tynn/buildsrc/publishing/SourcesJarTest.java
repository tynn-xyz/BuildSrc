package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.api.LibraryVariant;
import com.android.builder.model.BuildType;
import com.android.builder.model.ProductFlavor;
import com.android.builder.model.SourceProvider;

import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.HasConvention;
import org.gradle.api.plugins.Convention;
import org.gradle.api.provider.Property;
import org.gradle.jvm.tasks.Jar;
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Set;

import static com.android.builder.model.AndroidProject.FD_OUTPUTS;
import static java.nio.file.Paths.get;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

@SuppressWarnings({"unchecked", "WeakerAccess"})
@ExtendWith(MockitoExtension.class)
class SourcesJarTest {

    String destinationDir = "destinationDir";
    @Mock
    LibraryVariant variant;

    SourcesJar sources;

    @BeforeEach
    void setup() {
        sources = spy(new SourcesJar(variant, destinationDir));
    }

    @Test
    void executeShouldSetArchiveClassifier() {
        Jar jar = mock(Jar.class, RETURNS_DEEP_STUBS);
        String classifier = "classifier";
        Property<String> archiveClassifier = mock(Property.class);
        doReturn(archiveClassifier).when(jar).getArchiveClassifier();
        doReturn(new File("")).when(sources).getDestinationDir(any());
        doReturn(classifier).when(sources).getVariantClassifier(any());
        doReturn(emptySet()).when(sources).getVariantSourceDirectories(any());

        sources.execute(jar);

        verify(archiveClassifier).set(classifier);
    }

    @Test
    void executeShouldSetDestinationDirectory() {
        Jar jar = mock(Jar.class, RETURNS_DEEP_STUBS);
        File destinationDir = new File("foobar");
        DirectoryProperty destinationDirectory = mock(DirectoryProperty.class);
        Project project = mock(Project.class);
        doReturn(project).when(jar).getProject();
        doReturn(destinationDirectory).when(jar).getDestinationDirectory();
        doReturn(destinationDir).when(sources).getDestinationDir(any());
        doReturn("").when(sources).getVariantClassifier(any());
        doReturn(emptySet()).when(sources).getVariantSourceDirectories(any());

        sources.execute(jar);

        verify(destinationDirectory).set(destinationDir);
    }

    @Test
    void executeShouldSetFromVariantSources() {
        Jar jar = mock(Jar.class, RETURNS_DEEP_STUBS);
        File destinationDir = new File("foobar");
        Set<File> directories = singleton(destinationDir);
        doReturn(new File("")).when(sources).getDestinationDir(any());
        doReturn("").when(sources).getVariantClassifier(any());
        doReturn(directories).when(sources).getVariantSourceDirectories(any());

        sources.execute(jar);

        verify(jar).from(directories);
    }

    @Test
    void getDestinationDirShouldPointToOutputsDestinationDir() {
        String buildDir = "buildDir";
        Project project = mock(Project.class);
        doReturn(new File(buildDir)).when(project).getBuildDir();

        assertEquals(get(buildDir, FD_OUTPUTS, destinationDir).toFile(), sources.getDestinationDir(project));
    }

    @Test
    void getVariantClassifierShouldFormatVariantNames() {
        ProductFlavor flavor1 = mock(ProductFlavor.class);
        doReturn("flavor1").when(flavor1).getName();
        ProductFlavor flavor2 = mock(ProductFlavor.class);
        doReturn("flavor2").when(flavor2).getName();
        BuildType buildType = mock(BuildType.class);
        doReturn("buildType").when(buildType).getName();
        doReturn(buildType).when(variant).getBuildType();
        doReturn(asList(flavor1, flavor2)).when(variant).getProductFlavors();

        assertEquals("flavor1-flavor2-buildType", sources.getVariantClassifier(variant));
    }

    @Test
    void getVariantSourceDirectoriesShouldIncludeAllJavaDirectories() {
        File directory = new File("foobar");
        SourceProvider androidSources = mock(SourceProvider.class);
        doReturn(singleton(directory)).when(androidSources).getJavaDirectories();
        doReturn(singletonList(androidSources)).when(variant).getSourceSets();

        assertEquals(singleton(directory), sources.getVariantSourceDirectories(variant));
    }

    @Test
    void getVariantSourceDirectoriesShouldIncludeAllKotlinDirectories() {
        File directory = new File("foobar");
        MockSettings withConvention = withSettings().extraInterfaces(HasConvention.class);
        SourceProvider androidSources = mock(SourceProvider.class, withConvention);
        Convention convention = mock(Convention.class);
        KotlinSourceSet kotlinSources = mock(KotlinSourceSet.class);
        SourceDirectorySet directorySet = mock(SourceDirectorySet.class);
        doReturn(emptySet()).when(androidSources).getJavaDirectories();
        doReturn(convention).when((HasConvention) androidSources).getConvention();
        doReturn(kotlinSources).when(convention).getPlugin(any());
        doReturn(directorySet).when(kotlinSources).getKotlin();
        doReturn(singleton(directory)).when(directorySet).getSrcDirs();
        doReturn(singletonList(androidSources)).when(variant).getSourceSets();

        assertEquals(singleton(directory), sources.getVariantSourceDirectories(variant));
        verify(directorySet).getSrcDirs();
    }
}
