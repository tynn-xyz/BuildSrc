//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Action;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.attributes.Bundling;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.DocsType;
import org.gradle.api.attributes.Usage;
import org.gradle.api.model.ObjectFactory;

import static org.gradle.api.attributes.Bundling.BUNDLING_ATTRIBUTE;
import static org.gradle.api.attributes.Bundling.EXTERNAL;
import static org.gradle.api.attributes.Category.CATEGORY_ATTRIBUTE;
import static org.gradle.api.attributes.Category.DOCUMENTATION;
import static org.gradle.api.attributes.DocsType.DOCS_TYPE_ATTRIBUTE;
import static org.gradle.api.attributes.DocsType.SOURCES;
import static org.gradle.api.attributes.Usage.JAVA_RUNTIME;
import static org.gradle.api.attributes.Usage.USAGE_ATTRIBUTE;

final class SourcesAttributes implements Action<AttributeContainer> {

    private final ObjectFactory objects;

    SourcesAttributes(ObjectFactory objectFactory) {
        this.objects = objectFactory;
    }

    @Override
    public void execute(AttributeContainer attributes) {
        attributes.attribute(BUNDLING_ATTRIBUTE, objects.named(Bundling.class, EXTERNAL));
        attributes.attribute(CATEGORY_ATTRIBUTE, objects.named(Category.class, DOCUMENTATION));
        attributes.attribute(DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.class, SOURCES));
        attributes.attribute(USAGE_ATTRIBUTE, objects.named(Usage.class, JAVA_RUNTIME));
    }
}
