//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.elements

import groovy.transform.PackageScope
import org.junit.jupiter.api.io.TempDir

import java.util.zip.ZipFile

import static org.gradle.testkit.runner.GradleRunner.create
import static org.junit.jupiter.params.provider.Arguments.arguments

class KdocElementsPluginFuncTestBase {

    @TempDir
    @PackageScope
    File projectDir

    File buildFile

    def prepareGradleRunner(gradleVersion) {
        return create()
                .withProjectDir(projectDir)
                .withGradleVersion(gradleVersion)
    }

    static assertKdoc(group, artifactId, version) {
        def name = "$artifactId-$version-kdoc.jar"
        assertKdoc new File(group, "$artifactId/$version/$name")
    }

    static assertKdoc(File artifact) {
        assert artifact.exists()
        new ZipFile(artifact).with {
            assert entries().find {
                it.name =~ /^[^\/]+\/com.example.test\/index.html$/
            }
            assert entries().find {
                it.name =~ /global-ext-function.html$/
            }
            assert entries().find {
                it.name =~ /-main-example\/index.html$/
            }
        }
    }

    static gradleVersions() {
        ['7.0.2', '7.1.1', '7.2'].collect {
            arguments it
        }
    }
}
