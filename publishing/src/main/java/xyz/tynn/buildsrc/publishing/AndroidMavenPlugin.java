//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

import javax.annotation.Nonnull;

/**
 * A {@link Plugin} which extends the capabilities of the {@link LibraryPlugin Android library plugin}.
 * It defines maven publications for all Android release variants to be published with the variant
 * components. Each product flavor defines a unique artifact id within the scope of the project.
 */
public final class AndroidMavenPlugin extends AbstractPublishingPlugin {

    @Override
    public void execute(@Nonnull ProjectContext context) {
        context.applyPlugin(MavenPublishPlugin.class);
        super.execute(context);
    }

    @Override
    Action<LibraryVariant> getVariantAction(ProjectContext context) {
        return context.getMavenAction();
    }
}
