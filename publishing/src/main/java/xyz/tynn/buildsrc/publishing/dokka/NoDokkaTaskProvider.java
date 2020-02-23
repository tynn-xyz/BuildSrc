//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing.dokka;

import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.Transformer;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
final class NoDokkaTaskProvider<T extends Task> implements TaskProvider<T> {

    @Override
    public void configure(Action<? super T> action) {
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public T get() {
        return null;
    }

    @Nullable
    @Override
    public T getOrNull() {
        return null;
    }

    @Override
    public T getOrElse(T defaultValue) {
        return null;
    }

    @Override
    public <S> Provider<S> map(Transformer<? extends S, ? super T> transformer) {
        return null;
    }

    @Override
    public <S> Provider<S> flatMap(Transformer<? extends Provider<? extends S>, ? super T> transformer) {
        return null;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public Provider<T> orElse(T value) {
        return null;
    }

    @Override
    public Provider<T> orElse(Provider<? extends T> provider) {
        return null;
    }
}
