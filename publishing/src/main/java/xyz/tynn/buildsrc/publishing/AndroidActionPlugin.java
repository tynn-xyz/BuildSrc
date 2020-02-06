//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.InvalidUserCodeException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import javax.annotation.Nonnull;

import static xyz.tynn.buildsrc.publishing.Scoped.ANDROID_REQUIRED;

abstract class AndroidActionPlugin implements Plugin<Project> {

    @Override
    public final void apply(@Nonnull Project project) {
        try {
            ProjectContext context = getProjectContext(project);
            Action<LibraryVariant> variantAction = getVariantAction(context);
            context.withLibraryPlugin(context.getPluginAction(variantAction));
        } catch (NoClassDefFoundError e) {
            throw new InvalidUserCodeException(ANDROID_REQUIRED, e);
        }
    }

    abstract Action<LibraryVariant> getVariantAction(ProjectContext context);

    final ProjectContext getProjectContext(Project project) {
        return new ProjectContext(project);
    }
}
