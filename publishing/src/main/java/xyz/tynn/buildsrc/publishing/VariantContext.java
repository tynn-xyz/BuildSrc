//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.Transformer;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.jvm.tasks.Jar;

class VariantContext {

    private final ProjectContext context;
    private final LibraryVariant variant;

    VariantContext(ProjectContext context, LibraryVariant variant) {
        this.context = context;
        this.variant = variant;
    }

    Configuration getConfiguration(ArtifactScope scope) {
        return context.getConfiguration(scope.getPublicationName(variant.getName()));
    }

    Action<AttributeContainer> getArtifactAttributes(ArtifactScope scope) {
        return context.getArtifactAttributes(scope.getArtifactAttributes(this));
    }

    TaskProvider<Jar> getArtifactJar(ArtifactScope scope) {
        String name = scope.getJarName(variant.getName());
        try {
            return context.getArtifactJar(name);
        } catch (UnknownTaskException e) {
            return context.getArtifactJar(name, scope.getJarConfig(this));
        }
    }

    AdhocComponentWithVariants getComponent(ArtifactScope scope) {
        return context.getComponent(scope.getComponentName(variant.getName()));
    }

    Action<AttributeContainer> getFlavorAttributes() {
        return context.getFlavorAttributes(variant);
    }

    Action<AttributeContainer> getSourcesAttributes() {
        return context.getSourcesAttributes();
    }

    Action<Jar> getSourcesJar(String destinationDir) {
        return context.getSourcesJar(variant, destinationDir);
    }

    Transformer<PublishArtifact, AbstractArchiveTask> getVariantArtifact(ArtifactScope scope) {
        return context.getVariantArtifact(scope.getArtifactClassifier(variant.getName()));
    }

    Action<AttributeContainer> getVariantAttributes() {
        return context.getVariantAttributes(variant);
    }
}
