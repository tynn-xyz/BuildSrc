//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.build.api.attributes.BuildTypeAttr;
import com.android.build.api.attributes.VariantAttr;
import com.android.build.gradle.api.LibraryVariant;
import com.android.builder.model.BuildType;

import org.gradle.api.Project;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class VariantAttributesTest {

    @Mock
    BuildType buildType;
    @Mock
    LibraryVariant variant;

    @Test
    @SuppressWarnings("ConstantConditions")
    void executeShouldAddVariantAttributes() {
        String buildTypeName = "buildTypeName";
        String variantName = "variantName";
        doReturn(buildTypeName).when(buildType).getName();
        doReturn(buildType).when(variant).getBuildType();
        doReturn(variantName).when(variant).getName();
        Project project = ProjectBuilder.builder().build();
        AttributeContainer attributes = project.getConfigurations().create("test").getAttributes();

        new VariantAttributes(variant, project.getObjects()).execute(attributes);

        assertEquals(buildTypeName, attributes.getAttribute(BuildTypeAttr.ATTRIBUTE).getName());
        assertEquals(variantName, attributes.getAttribute(VariantAttr.ATTRIBUTE).getName());
    }
}
