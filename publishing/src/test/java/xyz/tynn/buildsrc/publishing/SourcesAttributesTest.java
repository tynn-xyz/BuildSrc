//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Project;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.gradle.api.attributes.Bundling.BUNDLING_ATTRIBUTE;
import static org.gradle.api.attributes.Bundling.EXTERNAL;
import static org.gradle.api.attributes.Category.CATEGORY_ATTRIBUTE;
import static org.gradle.api.attributes.Category.DOCUMENTATION;
import static org.gradle.api.attributes.DocsType.DOCS_TYPE_ATTRIBUTE;
import static org.gradle.api.attributes.DocsType.SOURCES;
import static org.gradle.api.attributes.Usage.JAVA_RUNTIME;
import static org.gradle.api.attributes.Usage.USAGE_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SourcesAttributesTest {

    @Test
    @SuppressWarnings("ConstantConditions")
    void executeShouldAddSourcesAttributes() {
        Project project = ProjectBuilder.builder().build();
        AttributeContainer attributes = project.getConfigurations().create("test").getAttributes();

        new SourcesAttributes(project.getObjects()).execute(attributes);

        assertEquals(EXTERNAL, attributes.getAttribute(BUNDLING_ATTRIBUTE).getName());
        assertEquals(DOCUMENTATION, attributes.getAttribute(CATEGORY_ATTRIBUTE).getName());
        assertEquals(SOURCES, attributes.getAttribute(DOCS_TYPE_ATTRIBUTE).getName());
        assertEquals(JAVA_RUNTIME, attributes.getAttribute(USAGE_ATTRIBUTE).getName());
    }
}
