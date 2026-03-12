//  Copyright 2023 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.test;

import static org.gradle.util.internal.DefaultGradleVersion.current;

@SuppressWarnings("unused")
public enum GradleVersion {
    GRADLE_8_14("8.14.4"),
    GRADLE_9_3("9.3.1"),
    GRADLE_9_4("9.4.0"),
    LATEST("latest") {
        @Override
        public String toString() {
            return current().getVersion();
        }
    };

    private final String version;

    GradleVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return version;
    }
}
