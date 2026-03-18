//  Copyright 2023 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName('xyz.tynn.convention.bom')
class XyzTynnConventionBomPluginTest {

    Project project

    @BeforeEach
    void setup() {
        project = ProjectBuilder
                .builder()
                .build()
        project.apply plugin: XyzTynnConventionBomPlugin
    }

    @Test
    void 'should apply java-platform'() {
        project.plugins.with {
            assert hasPlugin(XyzTynnConventionBomPlugin)
            assert hasPlugin(JavaPlatformPlugin)
        }
    }
}
