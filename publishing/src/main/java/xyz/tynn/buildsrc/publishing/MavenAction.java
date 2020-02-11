//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.gradle.api.LibraryVariant;
import com.android.builder.model.ProductFlavor;

import org.gradle.api.Action;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import static xyz.tynn.buildsrc.publishing.ZeroUtils.join;
import static xyz.tynn.buildsrc.publishing.ZeroUtils.joinCapitalized;

class MavenAction implements Action<LibraryVariant> {

    private final ProjectContext context;

    MavenAction(ProjectContext context) {
        this.context = context;
    }

    @Override
    public void execute(@Nonnull LibraryVariant variant) {
        if (variant.getBuildType().getName().equals("release")) {
            MavenPublication publication = getMavenPublication(variant.getName());
            publication.from(context.getComponent(variant.getName()));
            publication.setArtifactId(getArtifactId(variant.getProductFlavors()));
        }
    }

    String getArtifactId(List<ProductFlavor> productFlavors) {
        String projectName = context.getProjectName();
        if (productFlavors.isEmpty())
            return projectName;
        ArrayList<String> parts = new ArrayList<>();
        parts.add(projectName);
        for (ProductFlavor flavor : productFlavors)
            parts.add(flavor.getName());
        return join(parts);
    }

    MavenPublication getMavenPublication(String variantName) {
        PublishingExtension publishing = context.getPublishingExtension();
        PublicationContainer publications = publishing.getPublications();
        return publications.create(variantName, MavenPublication.class);
    }
}
