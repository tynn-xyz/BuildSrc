//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.Action;
import org.gradle.api.component.ConfigurationVariantDetails;

final class MavenMapping implements Action<ConfigurationVariantDetails> {

    private final String mavenScope;

    MavenMapping(String mavenScope) {
        this.mavenScope = mavenScope;
    }

    @Override
    public void execute(ConfigurationVariantDetails details) {
        details.mapToMavenScope(mavenScope);
        details.mapToOptional();
    }
}
