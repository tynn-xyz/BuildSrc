//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.attributes.AttributeContainer;

import javax.annotation.Nonnull;

final class VariantAttributes extends ArtifactAttributes {

    private final VariantContext context;

    VariantAttributes(VariantContext context, PublishingScope scope) {
        super(context, scope);
        this.context = context;
    }

    @Override
    public void execute(@Nonnull AttributeContainer attributes) {
        super.execute(attributes);
        context.setBuildTypeAttribute(attributes);
        context.setVariantAttribute(attributes);
        context.setProductFlavorAttributes(attributes);
    }
}
