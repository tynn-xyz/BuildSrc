//  Copyright 2023 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package conventions.plugins

import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import xyz.tynn.buildsrc.test.GradleVersion
import xyz.tynn.buildsrc.test.SyntheticProjectTest

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class WrapperTest extends SyntheticProjectTest {
    static final PROPS = '/gradle/wrapper/gradle-wrapper.properties'

    trait TestScripts {

        @ParameterizedTest(name = "Gradle={0}")
        @EnumSource
        void 'should create full gradle wrapper properties'(GradleVersion gradleVersion) {
            buildFile << """
                plugins {
                    id("xyz.tynn.convention.project")
                }
            """

            final result = createGradleRunner(gradleVersion)
                    .withArguments('wrapper')
                    .build()

            assert result.task(":wrapper").outcome == SUCCESS

            final properties = new Properties()
            projectFile(PROPS).withInputStream(properties::load)
            assert properties.distributionUrl.endsWith("gradle-${gradleVersion}-all.zip")
        }
    }

    @Nested
    class Groovy extends SyntheticProjectTest.Groovy implements TestScripts {
        Groovy() { super(WrapperTest.this) }
    }

    @Nested
    class Kotlin extends SyntheticProjectTest.Kotlin implements TestScripts {
        Kotlin() { super(WrapperTest.this) }
    }
}
