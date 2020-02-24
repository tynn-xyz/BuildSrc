//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing.dokka;

import org.gradle.api.Action;
import org.gradle.api.DomainObjectCollection;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.NamedDomainObjectCollectionSchema;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Namer;
import org.gradle.api.Rule;
import org.gradle.api.Task;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.provider.Provider;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import groovy.lang.Closure;

@SuppressWarnings({"EqualsWhichDoesntCheckParameterClass", "NullableProblems", "SuspiciousToArrayCall", "unchecked"})
final class NoDokkaTaskTasks implements TaskContainer, InvocationHandler {

    private final TaskContainer tasks;

    NoDokkaTaskTasks(TaskContainer tasks) {
        this.tasks = tasks;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) {
        return null;
    }

    @Override
    @Nullable
    public Task findByPath(String path) {
        return tasks.findByPath(path);
    }

    @Override
    public Task getByPath(String path) throws UnknownTaskException {
        return tasks.getByPath(path);
    }

    @Override
    public Task create(Map<String, ?> options) throws InvalidUserDataException {
        return create("", Task.class);
    }

    @Override
    public Task create(Map<String, ?> options, Closure configureClosure) throws InvalidUserDataException {
        return create("", Task.class);
    }

    @Override
    public Task create(String name, Closure configureClosure) throws InvalidUserDataException {
        return create(name, Task.class);
    }

    @Override
    public Task create(String name) throws InvalidUserDataException {
        return create(name, Task.class);
    }

    @Override
    public <T extends Task> T create(String name, Class<T> type) throws InvalidUserDataException {
        return (T) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{type},
                this
        );
    }

    @Override
    public <T extends Task> T create(String name, Class<T> type, Object... constructorArgs) throws InvalidUserDataException {
        return create(name, type);
    }

    @Override
    public <T extends Task> T create(String name, Class<T> type, Action<? super T> configuration) throws InvalidUserDataException {
        return create(name, type);
    }

    @Override
    public TaskProvider<Task> register(String name, Action<? super Task> configurationAction) throws InvalidUserDataException {
        return register(name, Task.class);
    }

    @Override
    public <T extends Task> TaskProvider<T> register(String name, Class<T> type, Action<? super T> configurationAction) throws InvalidUserDataException {
        return register(name, type);
    }

    @Override
    public <T extends Task> TaskProvider<T> register(String name, Class<T> type) throws InvalidUserDataException {
        return new NoDokkaTaskProvider<>();
    }

    @Override
    public <T extends Task> TaskProvider<T> register(String name, Class<T> type, Object... constructorArgs) throws InvalidUserDataException {
        return register(name, type);
    }

    @Override
    public TaskProvider<Task> register(String name) throws InvalidUserDataException {
        return register(name, Task.class);
    }

    @Override
    public Task replace(String name) {
        return tasks.replace(name);
    }

    @Override
    public <T extends Task> T replace(String name, Class<T> type) {
        return tasks.replace(name, type);
    }

    @Override
    public TaskCollection<Task> matching(Spec<? super Task> spec) {
        return tasks.matching(spec);
    }

    @Override
    public TaskCollection<Task> matching(Closure closure) {
        return tasks.matching(closure);
    }

    @Override
    public Task getByName(String name, Closure configureClosure) throws UnknownTaskException {
        return tasks.getByName(name, configureClosure);
    }

    @Override
    public Task getByName(String name) throws UnknownTaskException {
        return tasks.getByName(name);
    }

    @Override
    public <S extends Task> TaskCollection<S> withType(Class<S> type) {
        return tasks.withType(type);
    }

    @Override
    public Action<? super Task> whenTaskAdded(Action<? super Task> action) {
        return tasks.whenTaskAdded(action);
    }

    @Override
    public void whenTaskAdded(Closure closure) {
        tasks.whenTaskAdded(closure);
    }

    @Override
    public Task getAt(String name) throws UnknownTaskException {
        return tasks.getAt(name);
    }

    @Override
    public TaskProvider<Task> named(String name) throws UnknownTaskException {
        return tasks.named(name);
    }

    @Override
    public TaskProvider<Task> named(String name, Action<? super Task> configurationAction) throws UnknownTaskException {
        return tasks.named(name, configurationAction);
    }

    @Override
    public <S extends Task> TaskProvider<S> named(String name, Class<S> type) throws UnknownTaskException {
        return tasks.named(name, type);
    }

    @Override
    public <S extends Task> TaskProvider<S> named(String name, Class<S> type, Action<? super S> configurationAction) throws UnknownTaskException {
        return tasks.named(name, type, configurationAction);
    }

    @Override
    public Set<Task> findAll(Closure spec) {
        return tasks.findAll(spec);
    }

    @Override
    public boolean add(Task e) {
        return tasks.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends Task> c) {
        return tasks.addAll(c);
    }

    @Override
    public Namer<Task> getNamer() {
        return tasks.getNamer();
    }

    @Override
    public SortedMap<String, Task> getAsMap() {
        return tasks.getAsMap();
    }

    @Override
    public SortedSet<String> getNames() {
        return tasks.getNames();
    }

    @Override
    @Nullable
    public Task findByName(String name) {
        return tasks.findByName(name);
    }

    @Override
    public Task getByName(String name, Action<? super Task> configureAction) throws UnknownDomainObjectException {
        return tasks.getByName(name, configureAction);
    }

    @Override
    public Rule addRule(Rule rule) {
        return tasks.addRule(rule);
    }

    @Override
    public Rule addRule(String description, Closure ruleAction) {
        return tasks.addRule(description, ruleAction);
    }

    @Override
    public Rule addRule(String description, Action<String> ruleAction) {
        return tasks.addRule(description, ruleAction);
    }

    @Override
    public List<Rule> getRules() {
        return tasks.getRules();
    }

    @Override
    @Internal
    public NamedDomainObjectCollectionSchema getCollectionSchema() {
        return tasks.getCollectionSchema();
    }

    @Override
    public void addLater(Provider<? extends Task> provider) {
        tasks.addLater(provider);
    }

    @Override
    public void addAllLater(Provider<? extends Iterable<Task>> provider) {
        tasks.addAllLater(provider);
    }

    @Override
    public <S extends Task> DomainObjectCollection<S> withType(Class<S> type, Action<? super S> configureAction) {
        return tasks.withType(type, configureAction);
    }

    @Override
    public <S extends Task> DomainObjectCollection<S> withType(Class<S> type, Closure configureClosure) {
        return tasks.withType(type, configureClosure);
    }

    @Override
    public Action<? super Task> whenObjectAdded(Action<? super Task> action) {
        return tasks.whenObjectAdded(action);
    }

    @Override
    public void whenObjectAdded(Closure action) {
        tasks.whenObjectAdded(action);
    }

    @Override
    public Action<? super Task> whenObjectRemoved(Action<? super Task> action) {
        return tasks.whenObjectRemoved(action);
    }

    @Override
    public void whenObjectRemoved(Closure action) {
        tasks.whenObjectRemoved(action);
    }

    @Override
    public void all(Action<? super Task> action) {
        tasks.all(action);
    }

    @Override
    public void all(Closure action) {
        tasks.all(action);
    }

    @Override
    public void configureEach(Action<? super Task> action) {
        tasks.configureEach(action);
    }

    @Override
    public int size() {
        return tasks.size();
    }

    @Override
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return tasks.contains(o);
    }

    @NotNull
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return tasks.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] ts) {
        return tasks.toArray(ts);
    }

    @Override
    public boolean remove(Object o) {
        return tasks.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return tasks.containsAll(collection);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        return tasks.removeAll(collection);
    }

    @Override
    public boolean removeIf(Predicate<? super Task> predicate) {
        return tasks.removeIf(predicate);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        return tasks.retainAll(collection);
    }

    @Override
    public void clear() {
        tasks.clear();
    }

    @Override
    public boolean equals(Object o) {
        return tasks.equals(o);
    }

    @Override
    public int hashCode() {
        return tasks.hashCode();
    }

    @Override
    public Spliterator<Task> spliterator() {
        return tasks.spliterator();
    }

    @Override
    public Stream<Task> stream() {
        return tasks.stream();
    }

    @Override
    public Stream<Task> parallelStream() {
        return tasks.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super Task> consumer) {
        tasks.forEach(consumer);
    }

    @Override
    public <U extends Task> U maybeCreate(String name, Class<U> type) throws InvalidUserDataException {
        return tasks.maybeCreate(name, type);
    }

    @Override
    public <U extends Task> NamedDomainObjectContainer<U> containerWithType(Class<U> type) {
        return tasks.containerWithType(type);
    }

    @Override
    public Task maybeCreate(String name) {
        return tasks.maybeCreate(name);
    }

    @Override
    public Task create(String name, Action<? super Task> configureAction) throws InvalidUserDataException {
        return tasks.create(name, configureAction);
    }

    @Override
    public NamedDomainObjectContainer<Task> configure(Closure configureClosure) {
        return tasks.configure(configureClosure);
    }
}
