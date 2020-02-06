//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.builder.model.BaseConfig;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static xyz.tynn.buildsrc.publishing.ZeroUtils.join;
import static xyz.tynn.buildsrc.publishing.ZeroUtils.joinCapitalized;

class ZeroUtilsTest {

    @Test
    void joinShouldConcatWordsWithDash() {
        assertEquals("foo-bar", join(asList("foo", "bar")));
    }

    @Test
    void joinCapitalizedShouldBeEmptyWithoutWords() {
        assertEquals("", joinCapitalized());
    }

    @Test
    void joinCapitalizedShouldNotCapitalizeFirstLetter() {
        assertEquals("foo", joinCapitalized("", "", "foo", ""));
    }

    @Test
    void joinCapitalizedShouldFormatCamelCase() {
        assertEquals("fooBarBaz", joinCapitalized("", "foo", "", "bar", "", "baz", ""));
    }

    @Test
    void joinCapitalizedShouldNotCapitalizeWithEmptyPrefix() {
        BaseConfig config = mock(BaseConfig.class);
        when(config.getName()).thenReturn("name");

        assertEquals("name", joinCapitalized("", config));
    }

    @Test
    void joinCapitalizedShouldCapitalizeName() {
        BaseConfig config = mock(BaseConfig.class);
        when(config.getName()).thenReturn("name");

        assertEquals("fooName", joinCapitalized("foo", config));
    }
}
