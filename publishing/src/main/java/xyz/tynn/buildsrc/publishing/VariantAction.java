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

import static xyz.tynn.buildsrc.publishing.PluginMessage.ANDROID_UPDATE;

class VariantAction implements Action<LibraryVariant> {

    private final ProjectContext context;
    private final List<ArtifactScope> scopes;

    VariantAction(ProjectContext context, List<ArtifactScope> scopes) {
        this.context = context;
        this.scopes = scopes;
    }

    @Override
    public void execute(@Nonnull LibraryVariant variant) {
        try {
            VariantContext context = this.context.getVariantContext(variant);
            for (ArtifactScope scope : scopes) {
                TaskProvider<Jar> artifact = context.getArtifactJar(scope);
                Configuration configuration = context.getConfiguration(scope);
                configuration.getArtifacts().addLater(artifact.map(context.getVariantArtifact(scope)));
                configuration.attributes(context.getArtifactAttributes(scope));
                AdhocComponentWithVariants component = context.getComponent(scope);
                component.addVariantsFromConfiguration(configuration, this.context.getMavenMapping());
            }
        } catch (UnknownDomainObjectException e) {
            throw new InvalidUserCodeException(ANDROID_UPDATE, e);
        }
    }
}
