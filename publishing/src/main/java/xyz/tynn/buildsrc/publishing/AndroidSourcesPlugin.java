//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.InvalidUserCodeException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import javax.annotation.Nonnull;

import static xyz.tynn.buildsrc.publishing.ArtifactScope.ALL_VARIANT_SOURCES;
import static xyz.tynn.buildsrc.publishing.ArtifactScope.VARIANT_SOURCES;
import static xyz.tynn.buildsrc.publishing.PluginMessage.ANDROID_REQUIRED;

/**
 * A {@link Plugin} which extends the capabilities of the {@link LibraryPlugin Android library plugin}.
 * It defines sources Jar tasks and and configurations for all its build variants to be published with
 * the variant components.
 */
public final class AndroidSourcesPlugin implements Plugin<Project> {

    @Override
    public void apply(@Nonnull Project project) {
        try {
            ProjectContext context = getProjectContext(project);
            Action<LibraryVariant> variantAction = getVariantAction(context);
            context.withLibraryPlugin(context.getPluginAction(variantAction));
        } catch (NoClassDefFoundError e) {
            throw new InvalidUserCodeException(ANDROID_REQUIRED, e);
        }
    }

    private Action<LibraryVariant> getVariantAction(ProjectContext context) {
        return context.getVariantAction(ALL_VARIANT_SOURCES, VARIANT_SOURCES);
    }

    ProjectContext getProjectContext(Project project) {
        return new ProjectContext(project);
    }
}
