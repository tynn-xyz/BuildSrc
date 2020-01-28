//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.api.LibraryVariant;
import com.android.builder.model.BaseConfig;
import com.android.builder.model.SourceProvider;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.internal.HasConvention;
import org.gradle.api.plugins.Convention;
import org.gradle.jvm.tasks.Jar;
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.android.builder.model.AndroidProject.FD_OUTPUTS;
import static com.google.common.base.Joiner.on;

class SourcesJar implements Action<Jar> {

    private final LibraryVariant variant;
    private final String destinationDir;

    SourcesJar(LibraryVariant variant, String destinationDir) {
        this.variant = variant;
        this.destinationDir = destinationDir;
    }

    @Override
    public void execute(Jar jar) {
        jar.getDestinationDirectory().set(getDestinationDir(jar.getProject()));
        jar.getArchiveClassifier().set(getVariantClassifier(variant));
        jar.from(getVariantSourceDirectories(variant));
    }

    File getDestinationDir(Project project) {
        return new File(new File(project.getBuildDir(), FD_OUTPUTS), destinationDir);
    }

    String getVariantClassifier(LibraryVariant variant) {
        ArrayList<String> parts = new ArrayList<>();
        for (BaseConfig config : variant.getProductFlavors())
            parts.add(config.getName());
        parts.add(variant.getBuildType().getName());
        return on('-').join(parts);
    }

    Set<File> getVariantSourceDirectories(LibraryVariant variant) {
        HashSet<File> sourceDirectories = new HashSet<>();
        for (SourceProvider sourceSet : variant.getSourceSets()) {
            sourceDirectories.addAll(sourceSet.getJavaDirectories());
            try {
                Convention convention = ((HasConvention) sourceSet).getConvention();
                KotlinSourceSet kotlinSourceSet = convention.getPlugin(KotlinSourceSet.class);
                sourceDirectories.addAll(kotlinSourceSet.getKotlin().getSrcDirs());
            } catch (ClassCastException | IllegalStateException | NoClassDefFoundError ignored) {
            }
        }
        return sourceDirectories;
    }

}
