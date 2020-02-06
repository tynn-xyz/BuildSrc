//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import com.android.builder.model.BaseConfig;

import static com.android.utils.StringHelper.appendCamelCase;
import static com.google.common.base.Joiner.on;

class ZeroUtils {

    static String join(Iterable<String> words) {
        return on('-').join(words);
    }

    static String joinCapitalized(String... words) {
        int size = 0;
        for (String word : words)
            size += word.length();
        StringBuilder sb = new StringBuilder(size);
        for (String word : words)
            appendCamelCase(sb, word);
        return sb.toString();
    }

    static String joinCapitalized(String prefix, BaseConfig config) {
        if (prefix.isEmpty()) return config.getName();
        return joinCapitalized(prefix, config.getName());
    }
}
