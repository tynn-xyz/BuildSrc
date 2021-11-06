//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * A {@link Plugin} which extends the capabilities of the {@link LibraryPlugin Android library plugin}.
 * It defines maven publications for the release build type and all Android its variants to be
 * published with the variant components. Each build type component defines Maven dependencies and
 * attributes for all its product flavors. Each product flavor defines a unique artifact id within
 * the scope of the project.
 */
public final class AndroidMavenPlugin extends AbstractPublishingPlugin {

    private final SoftwareComponentFactory softwareComponentFactory;

    @Inject
    AndroidMavenPlugin(SoftwareComponentFactory softwareComponentFactory) {
        this.softwareComponentFactory = softwareComponentFactory;
    }

    @Override
    public void execute(@Nonnull ProjectContext context) {
        context.applyPlugin("maven-publish");
        super.execute(context);
    }

    @Override
    Action<LibraryVariant> getVariantAction(ProjectContext context) {
        return context.getMavenAction(softwareComponentFactory);
    }
}
