//  Copyright 2023 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.test;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

public abstract class SyntheticProjectTest {

    @TempDir
    protected File projectDir;
    protected File buildDir;

    @BeforeEach
    void setup() {
        buildDir = new File(projectDir, "build");
    }

    protected File projectFile(String path) {
        return new File(projectDir, path);
    }

    protected GradleRunner createGradleRunner(GradleVersion gradleVersion) {
        return GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(projectDir)
                .withGradleVersion(gradleVersion.toString());
    }

    protected abstract static class Scripts {
        protected File buildFile;
        protected File settingsFile;
    }

    protected abstract static class Groovy extends Scripts {

        private final SyntheticProjectTest test;

        protected Groovy(SyntheticProjectTest test) {
            this.test = test;
        }

        @BeforeEach
        void setup() {
            buildFile = new File(test.projectDir, "build.gradle");
            settingsFile = new File(test.projectDir, "settings.gradle");
        }
    }

    protected abstract static class Kotlin extends Scripts {

        private final SyntheticProjectTest test;

        protected Kotlin(SyntheticProjectTest test) {
            this.test = test;
        }

        @BeforeEach
        void setup() {
            buildFile = new File(test.projectDir, "build.gradle.kts");
            settingsFile = new File(test.projectDir, "settings.gradle.kts");
        }
    }
}
