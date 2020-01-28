//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Transformer;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

import javax.annotation.Nonnull;

final class VariantArtifact implements Transformer<PublishArtifact, AbstractArchiveTask> {

    private final String classifier;

    VariantArtifact(String classifier) {
        this.classifier = classifier;
    }

    @Override
    @Nonnull
    public ArchivePublishArtifact transform(@Nonnull AbstractArchiveTask task) {
        ArchivePublishArtifact artifact = new ArchivePublishArtifact(task);
        artifact.setClassifier(classifier);
        return artifact;
    }
}
