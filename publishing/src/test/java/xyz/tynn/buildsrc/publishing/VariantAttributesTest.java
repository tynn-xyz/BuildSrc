//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.attributes.AttributeContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.gradle.api.attributes.Bundling.BUNDLING_ATTRIBUTE;
import static org.gradle.api.attributes.Bundling.EXTERNAL;
import static org.gradle.api.attributes.Category.CATEGORY_ATTRIBUTE;
import static org.gradle.api.attributes.Category.DOCUMENTATION;
import static org.gradle.api.attributes.DocsType.DOCS_TYPE_ATTRIBUTE;
import static org.gradle.api.attributes.Usage.USAGE_ATTRIBUTE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VariantAttributesTest {

    @Mock
    AttributeContainer attributeContainer;

    @Mock
    VariantContext context;
    @Mock
    PublishingScope scope;

    @InjectMocks
    VariantAttributes attributes;

    @Test
    void executeShouldBuildTypeAttribute() {
        attributes.execute(attributeContainer);

        verify(context).setBuildTypeAttribute(attributeContainer);
    }

    @Test
    void executeShouldSetProductFlavorAttrAttributes() {
        attributes.execute(attributeContainer);

        verify(context).setProductFlavorAttributes(attributeContainer);
    }

    @Test
    void executeShouldSetExternalBundlingAttribute() {
        attributes.execute(attributeContainer);

        verify(context).setAttribute(attributeContainer, BUNDLING_ATTRIBUTE, EXTERNAL);
    }

    @Test
    void executeShouldSetDocumentationCategoryAttribute() {
        attributes.execute(attributeContainer);

        verify(context).setAttribute(attributeContainer, CATEGORY_ATTRIBUTE, DOCUMENTATION);
    }

    @Test
    void executeShouldSetSourcesDocsTypeAttribute() {
        String docsType = "docsType";
        when(scope.getDocsType()).thenReturn(docsType);

        attributes.execute(attributeContainer);

        verify(context).setAttribute(attributeContainer, DOCS_TYPE_ATTRIBUTE, docsType);
    }

    @Test
    void executeShouldSetJavaRuntimeUsageAttribute() {
        String usage = "usage";
        when(scope.getUsage()).thenReturn(usage);

        attributes.execute(attributeContainer);

        verify(context).setAttribute(attributeContainer, USAGE_ATTRIBUTE, usage);
    }
}
