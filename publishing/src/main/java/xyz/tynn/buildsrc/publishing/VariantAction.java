//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.InvalidUserCodeException;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;

import java.util.List;

import javax.annotation.Nonnull;

import static xyz.tynn.buildsrc.publishing.Scoped.ANDROID_UPDATE;

final class VariantAction implements Action<LibraryVariant> {

    private final ProjectContext context;
    private final List<PublishingScope> scopes;

    VariantAction(ProjectContext context, List<PublishingScope> scopes) {
        this.context = context;
        this.scopes = scopes;
    }

    @Override
    public void execute(@Nonnull LibraryVariant variant) {
        try {
            VariantContext context = this.context.getVariantContext(variant);
            for (PublishingScope scope : scopes)
                prepareConfiguration(context.getTaskContext(scope));
        } catch (UnknownDomainObjectException e) {
            throw new InvalidUserCodeException(ANDROID_UPDATE, e);
        }
    }

    void prepareConfiguration(TaskContext context) {
        TaskProvider<Jar> jarProvider = context.getJarProvider();
        Configuration configuration = context.getConfiguration();
        configuration.getArtifacts().addLater(jarProvider.map(context.getPublishArtifact()));
        configuration.attributes(context.getPublishingAttributes());
        AdhocComponentWithVariants component = context.getComponent();
        component.addVariantsFromConfiguration(configuration, context.getMavenMapping());
    }
}
