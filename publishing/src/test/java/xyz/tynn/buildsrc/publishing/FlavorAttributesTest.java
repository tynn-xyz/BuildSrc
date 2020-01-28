//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.api.attributes.ProductFlavorAttr;
import com.android.build.api.attributes.VariantAttr;
import com.android.build.gradle.api.LibraryVariant;
import com.android.builder.model.ProductFlavor;

import org.gradle.api.Project;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.singletonList;
import static org.gradle.api.attributes.Attribute.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class FlavorAttributesTest {

    @Mock
    ProductFlavor productFlavor;
    @Mock
    LibraryVariant variant;

    @Test
    @SuppressWarnings("ConstantConditions")
    void executeShouldAddFlavorAttributes() {
        String flavorDimension = "flavorDimension";
        String productFlavorName = "productFlavorName";
        doReturn(flavorDimension).when(productFlavor).getDimension();
        doReturn(productFlavorName).when(productFlavor).getName();
        doReturn(singletonList(productFlavor)).when(variant).getProductFlavors();
        Project project = ProjectBuilder.builder().build();
        AttributeContainer attributes = project.getConfigurations().create("test").getAttributes();

        new FlavorAttributes(variant, project.getObjects()).execute(attributes);

        assertEquals(productFlavorName, attributes.getAttribute(of(flavorDimension, ProductFlavorAttr.class)).getName());
    }
}
