//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.attributes.AttributeContainer;

import static xyz.tynn.buildsrc.publishing.ArtifactScope.JAVADOC;
import static xyz.tynn.buildsrc.publishing.ArtifactScope.KDOC;
import static xyz.tynn.buildsrc.publishing.ArtifactScope.SOURCES;

enum PublishingScope implements Named {

    // Javadoc
    ALL_VARIANT_JAVADOC(true, JAVADOC),
    VARIANT_JAVADOC(false, JAVADOC),

    // Kdoc
    ALL_VARIANT_KDOC(true, KDOC),
    VARIANT_KDOC(false, KDOC),

    // Sources
    ALL_VARIANT_SOURCES(true, SOURCES),
    VARIANT_SOURCES(false, SOURCES);

    private final ArtifactScope scope;
    private final boolean isAllPublication;

    PublishingScope(boolean isAllPublication, ArtifactScope scope) {
        this.isAllPublication = isAllPublication;
        this.scope = scope;
    }

    @Override
    public String getName() {
        return scope.getName();
    }

    String getArtifactClassifier(String name) {
        return isAllPublication ? name + '-' + scope.getName() : scope.getName();
    }

    String getComponentName(String name) {
        return isAllPublication ? "all" : name;
    }

    String getDocsType() {
        return scope.getName();
    }

    Scoped<?> getSourcePath() {
        return scope.getSourcePath();
    }

    String getMavenScope() {
        return scope.getMavenScope().getName();
    }

    String getOutputsDir() {
        return scope.getOutputsDir();
    }

    String getPublicationName(String name) {
        return ZeroUtils.joinCapitalized(name, isAllPublication ? "all" : "", scope.getName(), "publication");
    }

    Scoped<Action<AttributeContainer>> getPublishingAttributes() {
        return isAllPublication ?
                TaskContext::getVariantAttributes :
                TaskContext::getArtifactAttributes;
    }

    String getUsage() {
        return scope.getMavenScope().getUsage();
    }
}
