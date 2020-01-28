//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.api.attributes.BuildTypeAttr;
import com.android.build.api.attributes.VariantAttr;
import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.model.ObjectFactory;

final class VariantAttributes implements Action<AttributeContainer> {

    private final ObjectFactory objects;

    private final String name;
    private final String buildType;

    VariantAttributes(LibraryVariant variant, ObjectFactory objectFactory) {
        this.objects = objectFactory;
        this.name = variant.getName();
        this.buildType = variant.getBuildType().getName();
    }

    @Override
    public void execute(AttributeContainer attributes) {
        attributes.attribute(BuildTypeAttr.ATTRIBUTE, objects.named(BuildTypeAttr.class, buildType));
        attributes.attribute(VariantAttr.ATTRIBUTE, objects.named(VariantAttr.class, name));
    }
}
