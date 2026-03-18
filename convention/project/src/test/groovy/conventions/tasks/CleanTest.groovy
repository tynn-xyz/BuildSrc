//  Copyright 2023 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package conventions.tasks

import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import xyz.tynn.buildsrc.test.GradleVersion
import xyz.tynn.buildsrc.test.SyntheticProjectTest

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

class CleanTest extends SyntheticProjectTest {

    trait TestScripts {

        @ParameterizedTest(name = "Gradle={0}")
        @EnumSource
        void 'should create default clean task'(GradleVersion gradleVersion) {
            buildFile << """
                plugins {
                    id("xyz.tynn.convention.project")
                }
            """

            final result = createGradleRunner(gradleVersion)
                    .withArguments('clean')
                    .build()

            assert result.task(":clean").outcome == UP_TO_DATE
            assert !buildDir.exists()
        }

        @ParameterizedTest(name = "Gradle={0}")
        @EnumSource
        void 'should create default clean task deleting build'(GradleVersion gradleVersion) {
            buildFile << """
                plugins {
                    id("xyz.tynn.convention.project")
                }
                
                buildDir.mkdir()
            """

            final result = createGradleRunner(gradleVersion)
                    .withArguments('clean')
                    .build()

            assert result.task(":clean").outcome == SUCCESS
            assert !buildDir.exists()
        }

        @ParameterizedTest(name = "Gradle={0}")
        @EnumSource
        void 'should not create default clean with clean task'(GradleVersion gradleVersion) {
            buildFile << """
                plugins {
                    id("xyz.tynn.convention.project")
                }
                
                task("clean")
        
                buildDir.mkdir()
            """

            final result = createGradleRunner(gradleVersion)
                    .withArguments('clean')
                    .build()

            assert result.task(":clean").outcome == UP_TO_DATE
            assert buildDir.exists()
        }
    }

    @Nested
    class Groovy extends SyntheticProjectTest.Groovy implements TestScripts {
        Groovy() { super(CleanTest.this) }
    }

    @Nested
    class Kotlin extends SyntheticProjectTest.Kotlin implements TestScripts {
        Kotlin() { super(CleanTest.this) }
    }
}
