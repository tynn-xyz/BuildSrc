//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Action;
import org.gradle.api.attributes.AttributeContainer;

import java.util.List;

import javax.annotation.Nonnull;

final class ArtifactAttributes implements Action<AttributeContainer> {

    private final List<Action<AttributeContainer>> attributes;

    ArtifactAttributes(List<Action<AttributeContainer>> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void execute(@Nonnull AttributeContainer attributes) {
        for (Action<AttributeContainer> attribute : this.attributes)
            attribute.execute(attributes);
    }
}
