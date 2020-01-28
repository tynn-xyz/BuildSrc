//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Task;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.verify;

@SuppressWarnings("WeakerAccess")
@ExtendWith(MockitoExtension.class)
class VariantArtifactTest {

    String classifier = "classifier";

    @Mock(answer = RETURNS_DEEP_STUBS)
    AbstractArchiveTask task;

    VariantArtifact artifact = new VariantArtifact(classifier);

    @Test
    void transformShouldSetClassifierFromConfig() {
        assertEquals(classifier, artifact.transform(task).getClassifier());
    }

    @Test
    void transformShouldSetArtifactFromTask() {
        assertEquals(task, artifact.transform(task).getArchiveTask());
    }

    @Test
    void transformShouldSetTaskAsDependency() {
        Set<? extends Task> dependencies = artifact.transform(task)
                .getBuildDependencies()
                .getDependencies(null);

        assertTrue(dependencies.contains(task));
    }
}
