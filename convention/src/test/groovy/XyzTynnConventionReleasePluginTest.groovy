//  Copyright 2023 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

import org.gradle.api.Project
import org.gradle.plugins.signing.SigningPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static org.gradle.api.attributes.Category.CATEGORY_ATTRIBUTE
import static org.gradle.api.attributes.Category.DOCUMENTATION
import static org.gradle.api.attributes.DocsType.DOCS_TYPE_ATTRIBUTE
import static org.gradle.api.attributes.DocsType.SOURCES

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

        @ParameterizedTest
        @ValueSource(strings = ['java', 'java-library', 'org.jetbrains.kotlin.jvm'])
        void 'should release the java component with sources'(String plugin) {
            project.with {
                apply plugin: plugin
                assert components.java.usages.any {
                    it.attributes.with {
                        final category = getAttribute(CATEGORY_ATTRIBUTE)?.name
                        final docsType = getAttribute(DOCS_TYPE_ATTRIBUTE)?.name
                        category == DOCUMENTATION && docsType == SOURCES
                    }
                }
                assert components.release == components.java
            }
        }
    }
}
