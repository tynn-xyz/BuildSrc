//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.jvm.tasks.Jar;

import java.util.List;

import javax.annotation.Nonnull;

import static com.android.utils.StringHelper.appendCapitalized;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

enum ArtifactScope {

    ALL_VARIANT_SOURCES(true, Type.SOURCES),
    VARIANT_SOURCES(false, Type.SOURCES);

    private final Type type;
    private final boolean isAllPublication;

    ArtifactScope(boolean isAllPublication, Type type) {
        this.isAllPublication = isAllPublication;
        this.type = type;
    }

    List<Action<AttributeContainer>> getArtifactAttributes(VariantContext context) {
        if (!isAllPublication)
            return singletonList(type.getArtifactAttributes(context));
        return asList(
                type.getArtifactAttributes(context),
                context.getVariantAttributes(),
                context.getFlavorAttributes()
        );
    }

    String getArtifactClassifier(String name) {
        return isAllPublication ? name + '-' + type.getName() : type.getName();
    }

    String getComponentName(String name) {
        return isAllPublication ? "all" : name;
    }

    Action<Jar> getJarConfig(VariantContext context) {
        return type.getJarConfig(context);
    }

    String getJarName(String name) {
        return appendCapitalized(name, type.getName(), "Jar");
    }

    String getPublicationName(String name) {
        return appendCapitalized(name, isAllPublication ? "all" : "", type.getName(), "Publication");
    }

    private enum Type implements Named {

        SOURCES("sources") {
            @Override
            Action<AttributeContainer> getArtifactAttributes(VariantContext context) {
                return context.getSourcesAttributes();
            }

            @Override
            Action<Jar> getJarConfig(VariantContext context) {
                return context.getSourcesJar(getName());
            }
        };

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName() {
            return name;
        }

        abstract Action<AttributeContainer> getArtifactAttributes(VariantContext context);

        abstract Action<Jar> getJarConfig(VariantContext context);
    }
}
