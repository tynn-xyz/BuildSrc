//  Copyright 2021 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing

import static org.junit.jupiter.params.provider.Arguments.arguments

class AndroidGradleVersions {

    private static final kotlinVersion = '1.5.31'

    private static final gradleVersions = [
            '7.0.2',
            '7.1.1',
            '7.2',
    ]

    private static final androidVersions = [
            '7.0.+': gradleVersions,
    ]

    static provide() {
        androidVersions.collectMany { a, gradleVersions ->
            gradleVersions.collectMany { g ->
                [
                        arguments(a, g, kotlinVersion),
                        arguments(a, g, false),
                ]
            }
        }
    }

    static provideKotlinOnly() {
        androidVersions.collectMany { a, gradleVersions ->
            gradleVersions.collect { g ->
                arguments(a, g, kotlinVersion)
            }
        }
    }
}

