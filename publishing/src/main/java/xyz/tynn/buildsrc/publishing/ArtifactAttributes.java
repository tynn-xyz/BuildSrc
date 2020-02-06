//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Action;
import org.gradle.api.attributes.AttributeContainer;

import javax.annotation.Nonnull;

import static org.gradle.api.attributes.Bundling.BUNDLING_ATTRIBUTE;
import static org.gradle.api.attributes.Bundling.EXTERNAL;
import static org.gradle.api.attributes.Category.CATEGORY_ATTRIBUTE;
import static org.gradle.api.attributes.Category.DOCUMENTATION;
import static org.gradle.api.attributes.DocsType.DOCS_TYPE_ATTRIBUTE;
import static org.gradle.api.attributes.Usage.USAGE_ATTRIBUTE;

class ArtifactAttributes implements Action<AttributeContainer> {

    private final VariantContext context;
    private final PublishingScope scope;

    ArtifactAttributes(VariantContext context, PublishingScope scope) {
        this.context = context;
        this.scope = scope;
    }

    @Override
    public void execute(@Nonnull AttributeContainer attributes) {
        context.setAttribute(attributes, BUNDLING_ATTRIBUTE, EXTERNAL);
        context.setAttribute(attributes, CATEGORY_ATTRIBUTE, DOCUMENTATION);
        context.setAttribute(attributes, DOCS_TYPE_ATTRIBUTE, scope.getDocsType());
        context.setAttribute(attributes, USAGE_ATTRIBUTE, scope.getUsage());
    }
}
