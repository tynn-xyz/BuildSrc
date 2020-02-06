//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.junit.jupiter.api.Test;

import static org.gradle.api.attributes.Usage.JAVA_API;
import static org.gradle.api.attributes.Usage.JAVA_RUNTIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static xyz.tynn.buildsrc.publishing.MavenScope.COMPILE;
import static xyz.tynn.buildsrc.publishing.MavenScope.RUNTIME;

class MavenScopeTest {

    @Test
    void compileNameShouldBeCompile() {
        assertEquals("compile", COMPILE.getName());
    }

    @Test
    void compileUsageShouldBeJavaApi() {
        assertEquals(JAVA_API, COMPILE.getUsage());
    }

    @Test
    void runtimeNameShouldBeRuntime() {
        assertEquals("runtime", RUNTIME.getName());
    }

    @Test
    void runtimeUsageShouldBeJavaRuntime() {
        assertEquals(JAVA_RUNTIME, RUNTIME.getUsage());
    }
}
