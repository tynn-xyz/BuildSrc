//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import org.gradle.api.component.ConfigurationVariantDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MavenMappingTest {

    @Mock
    ConfigurationVariantDetails details;

    @InjectMocks
    MavenMapping mapping;

    @Test
    void executeShouldMapToMavenRuntime() {
        mapping.execute(details);

        verify(details).mapToMavenScope("runtime");
    }

    @Test
    void executeShouldMapToOptional() {
        mapping.execute(details);

        verify(details).mapToOptional();
    }

    @Test
    void executeShouldNotSkip() {
        mapping.execute(details);

        verify(details, never()).skip();
    }
}
