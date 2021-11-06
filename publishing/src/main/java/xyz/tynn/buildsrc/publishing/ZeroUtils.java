//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import static kotlin.text.StringsKt.capitalize;

import com.android.builder.model.BaseConfig;

import java.util.Iterator;

class ZeroUtils {

    static String join(Iterable<String> words) {
        Iterator<String> iter = words.iterator();
        if (!iter.hasNext()) return "";
        StringBuilder sb = new StringBuilder(iter.next());
        while (iter.hasNext())
            sb.append('-').append(iter.next());
        return sb.toString();
    }

    static String joinCapitalized(String prefix, BaseConfig config) {
        if (prefix.isEmpty()) return config.getName();
        return joinCapitalized(prefix, config.getName());
    }

    static String joinCapitalized(String... words) {
        if (words.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (String word : words)
            if (sb.isEmpty()) sb.append(word);
            else sb.append(capitalize(word));
        return sb.toString();
    }
}
