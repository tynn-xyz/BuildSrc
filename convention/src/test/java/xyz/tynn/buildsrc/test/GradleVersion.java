//  Copyright 2023 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.test;

import static org.gradle.util.internal.DefaultGradleVersion.current;

@SuppressWarnings("unused")
public enum GradleVersion {
    GRADLE_7_6("7.6.3"),
    GRADLE_8_0("8.0.2"),
    GRADLE_8_1("8.1.1"),
    GRADLE_8_2("8.2.1"),
    GRADLE_8_3("8.3"),
    GRADLE_8_4("8.4"),
    GRADLE_8_5("8.5"),
    LATEST("8.6-RC") {
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
