//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.api.attributes.ProductFlavorAttr;
import com.android.build.gradle.api.LibraryVariant;
import com.android.builder.model.ProductFlavor;

import org.gradle.api.Action;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import static org.gradle.api.attributes.Attribute.of;
import static org.gradle.util.GradleVersion.current;
import static org.gradle.util.GradleVersion.version;
import static xyz.tynn.buildsrc.publishing.ZeroUtils.join;

final class MavenAction implements Action<LibraryVariant> {

    private final ProjectContext context;
    private final SoftwareComponentFactory softwareComponentFactory;

    MavenAction(ProjectContext context, SoftwareComponentFactory softwareComponentFactory) {
        this.context = context;
        this.softwareComponentFactory = softwareComponentFactory;
    }

    @Override
    public void execute(@Nonnull LibraryVariant variant) {
        String componentName = variant.getBuildType().getName();
        if (componentName.equals("release")) {
            String name = variant.getName();
            Collection<ProductFlavor> flavors = variant.getProductFlavors();
            MavenPublication publication = getVariantPublication(name, flavors);
            if (!flavors.isEmpty() && isGradleWithModuleMetaData())
                configureConfiguration(name, componentName, flavors, publication);
        }
    }

    void configureAttributes(AttributeContainer attributes, Collection<ProductFlavor> flavors) {
        for (ProductFlavor flavor : flavors)
            context.setAttribute(attributes, getAttribute(flavor.getDimension()), flavor.getName());
    }

    void configureComponent(String name, Configuration configuration) {
        try {
            AdhocComponentWithVariants component = context.getComponent(name, softwareComponentFactory);
            component.addVariantsFromConfiguration(configuration, new MavenMapping("compile"));
            getMavenPublication(name).from(component);
        } catch (InvalidUserDataException ignored) {
        }
    }

    void configureConfiguration(String name, String componentName, Collection<ProductFlavor> flavors, MavenPublication publication) {
        Configuration configuration = context.getConfiguration(name + "MetaPublication");
        configuration.getDependencies().add(context.getProjectDependency(publication));
        configureAttributes(configuration.getAttributes(), flavors);
        configureComponent(componentName, configuration);
    }

    void setArtifactId(MavenPublication publication, Collection<ProductFlavor> productFlavors) {
        if (!productFlavors.isEmpty()) {
            ArrayList<String> parts = new ArrayList<>();
            parts.add(context.getProjectName());
            for (ProductFlavor flavor : productFlavors)
                parts.add(flavor.getName());
            publication.setArtifactId(join(parts));
        }
    }

    boolean isGradleWithModuleMetaData() {
        return version("6.0").compareTo(current()) <= 0;
    }

    Attribute<ProductFlavorAttr> getAttribute(String name) {
        if (!name.contains("."))
            name = context.getProjectGroup() + '.' + name;
        return of(name, ProductFlavorAttr.class);
    }

    MavenPublication getMavenPublication(String variantName) {
        PublishingExtension publishing = context.getPublishingExtension();
        PublicationContainer publications = publishing.getPublications();
        return publications.create(variantName, MavenPublication.class);
    }

    MavenPublication getVariantPublication(String variantName, Collection<ProductFlavor> flavors) {
        MavenPublication publication = getMavenPublication(variantName);
        publication.from(context.getComponent(variantName));
        setArtifactId(publication, flavors);
        return publication;
    }
}
