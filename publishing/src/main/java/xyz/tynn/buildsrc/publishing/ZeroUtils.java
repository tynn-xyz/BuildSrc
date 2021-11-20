//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import static java.lang.Character.toUpperCase;

import com.android.builder.model.BaseConfig;

class ZeroUtils {

    static String join(Iterable<String> words) {
        StringBuilder sb = new StringBuilder();
        for (String word : words)
            if (sb.length() == 0) sb.append(word);
            else sb.append('-').append(word);
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
            if (sb.length() == 0) sb.append(word);
            else appendCapitalized(sb, word);
        return sb.toString();
    }

    private static void appendCapitalized(StringBuilder sb, String word) {
        if (word.isEmpty()) return;
        char firstChar = toUpperCase(word.charAt(0));
        sb.append(firstChar);
        int length = word.length();
        if (word.length() == 1) return;
        sb.append(word.subSequence(1, length));
    }
}
