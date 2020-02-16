//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing;

import java.util.function.Function;

interface Scoped<T> extends Function<TaskContext, T> {

    String ANDROID_REQUIRED = "The Android Publishing plugin requires the Android Library plugin.";

    String ANDROID_UPDATE = "The Android Publishing plugin requires the Android Library plugin version 3.6.0.";
}
