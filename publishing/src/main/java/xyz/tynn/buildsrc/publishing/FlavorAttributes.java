//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.api.attributes.ProductFlavorAttr;
import com.android.build.gradle.api.LibraryVariant;
import com.android.builder.model.ProductFlavor;

import org.gradle.api.Action;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.model.ObjectFactory;

import java.util.List;

import static org.gradle.api.attributes.Attribute.of;

final class FlavorAttributes implements Action<AttributeContainer> {

    private final ObjectFactory objects;

    private final List<ProductFlavor> productFlavors;

    FlavorAttributes(LibraryVariant variant, ObjectFactory objectFactory) {
        this.objects = objectFactory;
        this.productFlavors = variant.getProductFlavors();
    }

    @Override
    public void execute(AttributeContainer attributes) {
        for (ProductFlavor flavor : productFlavors) {
            Attribute<ProductFlavorAttr> attribute = of(flavor.getDimension(), ProductFlavorAttr.class);
            attributes.attribute(attribute, objects.named(ProductFlavorAttr.class, flavor.getName()));
        }
    }
}
