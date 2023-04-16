//  Copyright 2023 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

import org.gradle.api.Project
import org.gradle.plugins.signing.SigningPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName('xyz.tynn.convention.release')
class XyzTynnConventionReleasePluginTest {

    Project project

    @BeforeEach
    void setup() {
        project = ProjectBuilder
                .builder()
                .build()
        project.apply plugin: XyzTynnConventionReleasePlugin
    }

    @Test
    void 'should be lazy'() {
        project.plugins.with {
            assert size() == 1
            assert hasPlugin(XyzTynnConventionReleasePlugin)
        }
    }

    @Nested
    @DisplayName('with maven-publish')
    class OnMavenPublish {

        @BeforeEach
        void setup() {
            project.apply plugin: 'maven-publish'
        }

        @Test
        void 'should apply signing'() {
            assert project.plugins.hasPlugin(SigningPlugin)
        }

        @Test
        void 'should release the javaPlatform'() {
            project.with {
                apply plugin: 'java-platform'
                assert components.release == components.javaPlatform
            }
        }

        @Test
        void 'should release the versionCatalog'() {
            project.with {
                apply plugin: 'version-catalog'
                assert components.release == components.versionCatalog
            }
        }
    }
}
