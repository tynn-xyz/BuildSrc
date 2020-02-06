//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.Plugin;

import static xyz.tynn.buildsrc.publishing.PublishingScope.ALL_VARIANT_JAVADOC;
import static xyz.tynn.buildsrc.publishing.PublishingScope.VARIANT_JAVADOC;

/**
 * A {@link Plugin} which extends the capabilities of the {@link LibraryPlugin Android library plugin}.
 * It defines javadoc configurations and Jar tasks for all Android build variants to be published with
 * the variant components.
 */
public final class AndroidJavadocPlugin extends AndroidActionPlugin {

    @Override
    Action<LibraryVariant> getVariantAction(ProjectContext context) {
        return context.getVariantAction(ALL_VARIANT_JAVADOC, VARIANT_JAVADOC);
    }
}
