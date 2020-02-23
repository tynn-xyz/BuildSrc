//  Copyright 2020 Christian Schmitz
//  SPDX-License-Identifier: Apache-2.0

package xyz.tynn.buildsrc.publishing.dokka;

import org.gradle.api.Action;
import org.gradle.api.AntBuilder;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.NamedDomainObjectFactory;
import org.gradle.api.PathValidation;
import org.gradle.api.Project;
import org.gradle.api.ProjectState;
import org.gradle.api.Task;
import org.gradle.api.UnknownProjectException;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.dsl.ArtifactHandler;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.dsl.DependencyLockingHandler;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.component.SoftwareComponentContainer;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.DeleteSpec;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.initialization.dsl.ScriptHandler;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.LoggingManager;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.ObjectConfigurationAction;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.resources.ResourceHandler;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.WorkResult;
import org.gradle.normalization.InputNormalizationHandler;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;
import org.gradle.process.JavaExecSpec;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import groovy.lang.Closure;
import groovy.lang.MissingPropertyException;

@SuppressWarnings("NullableProblems")
final class NoDokkaTaskProject implements Project {

    private final Project project;
    private TaskContainer tasks;

    NoDokkaTaskProject(Project project) {
        this.project = project;
    }

    private TaskContainer requireTasks() {
        if (tasks == null)
            tasks = new NoDokkaTaskTasks(project.getTasks());
        return tasks;
    }

    @Override
    public Project getRootProject() {
        return project.getRootProject();
    }

    @Override
    public File getRootDir() {
        return project.getRootDir();
    }

    @Override
    public File getBuildDir() {
        return project.getBuildDir();
    }

    @Override
    public void setBuildDir(File path) {
        project.setBuildDir(path);
    }

    @Override
    public void setBuildDir(Object path) {
        project.setBuildDir(path);
    }

    @Override
    public File getBuildFile() {
        return project.getBuildFile();
    }

    @Override
    @Nullable
    public Project getParent() {
        return project.getParent();
    }

    @Override
    public String getName() {
        return project.getName();
    }

    @Override
    public String getDisplayName() {
        return project.getDisplayName();
    }

    @Override
    @Nullable
    public String getDescription() {
        return project.getDescription();
    }

    @Override
    public void setDescription(@Nullable String description) {
        project.setDescription(description);
    }

    @Override
    public Object getGroup() {
        return project.getGroup();
    }

    @Override
    public void setGroup(Object group) {
        project.setGroup(group);
    }

    @Override
    public Object getVersion() {
        return project.getVersion();
    }

    @Override
    public void setVersion(Object version) {
        project.setVersion(version);
    }

    @Override
    public Object getStatus() {
        return project.getStatus();
    }

    @Override
    public void setStatus(Object status) {
        project.setStatus(status);
    }

    @Override
    public Map<String, Project> getChildProjects() {
        return project.getChildProjects();
    }

    @Override
    public void setProperty(String name, @Nullable Object value) throws MissingPropertyException {
        project.setProperty(name, value);
    }

    @Override
    public Project getProject() {
        return project.getProject();
    }

    @Override
    public Set<Project> getAllprojects() {
        return project.getAllprojects();
    }

    @Override
    public Set<Project> getSubprojects() {
        return project.getSubprojects();
    }

    @Override
    public Task task(String name) throws InvalidUserDataException {
        return requireTasks().create(name);
    }

    @Override
    public Task task(Map<String, ?> args, String name) throws InvalidUserDataException {
        HashMap<String, Object> map = new HashMap<>(args);
        map.put(Task.TASK_NAME, name);
        return requireTasks().create(map);
    }

    @Override
    public Task task(Map<String, ?> args, String name, Closure configureClosure) {
        return requireTasks().create(args, configureClosure);
    }

    @Override
    public Task task(String name, Closure configureClosure) {
        return requireTasks().create(name, configureClosure);
    }

    @Override
    public Task task(String name, Action<? super Task> configureAction) {
        return requireTasks().create(name, configureAction);
    }

    @Override
    public String getPath() {
        return project.getPath();
    }

    @Override
    public List<String> getDefaultTasks() {
        return project.getDefaultTasks();
    }

    @Override
    public void setDefaultTasks(List<String> defaultTasks) {
        project.setDefaultTasks(defaultTasks);
    }

    @Override
    public void defaultTasks(String... defaultTasks) {
        project.defaultTasks(defaultTasks);
    }

    @Override
    public Project evaluationDependsOn(String path) throws UnknownProjectException {
        return project.evaluationDependsOn(path);
    }

    @Override
    public void evaluationDependsOnChildren() {
        project.evaluationDependsOnChildren();
    }

    @Override
    @Nullable
    public Project findProject(String path) {
        return project.findProject(path);
    }

    @Override
    public Project project(String path) throws UnknownProjectException {
        return project.project(path);
    }

    @Override
    public Project project(String path, Closure configureClosure) {
        return project.project(path, configureClosure);
    }

    @Override
    public Project project(String path, Action<? super Project> configureAction) {
        return project.project(path, configureAction);
    }

    @Override
    public Map<Project, Set<Task>> getAllTasks(boolean recursive) {
        return project.getAllTasks(recursive);
    }

    @Override
    public Set<Task> getTasksByName(String name, boolean recursive) {
        return project.getTasksByName(name, recursive);
    }

    @Override
    public File getProjectDir() {
        return project.getProjectDir();
    }

    @Override
    public File file(Object path) {
        return project.file(path);
    }

    @Override
    public File file(Object path, PathValidation validation) throws InvalidUserDataException {
        return project.file(path, validation);
    }

    @Override
    public URI uri(Object path) {
        return project.uri(path);
    }

    @Override
    public String relativePath(Object path) {
        return project.relativePath(path);
    }

    @Override
    public ConfigurableFileCollection files(Object... paths) {
        return project.files(paths);
    }

    @Override
    public ConfigurableFileCollection files(Object paths, Closure configureClosure) {
        return project.files(paths, configureClosure);
    }

    @Override
    public ConfigurableFileCollection files(Object paths, Action<? super ConfigurableFileCollection> configureAction) {
        return project.files(paths, configureAction);
    }

    @Override
    public ConfigurableFileTree fileTree(Object baseDir) {
        return project.fileTree(baseDir);
    }

    @Override
    public ConfigurableFileTree fileTree(Object baseDir, Closure configureClosure) {
        return project.fileTree(baseDir, configureClosure);
    }

    @Override
    public ConfigurableFileTree fileTree(Object baseDir, Action<? super ConfigurableFileTree> configureAction) {
        return project.fileTree(baseDir, configureAction);
    }

    @Override
    public ConfigurableFileTree fileTree(Map<String, ?> args) {
        return project.fileTree(args);
    }

    @Override
    public FileTree zipTree(Object zipPath) {
        return project.zipTree(zipPath);
    }

    @Override
    public FileTree tarTree(Object tarPath) {
        return project.tarTree(tarPath);
    }

    @Override
    public <T> Provider<T> provider(Callable<T> value) {
        return project.provider(value);
    }

    @Override
    public ProviderFactory getProviders() {
        return project.getProviders();
    }

    @Override
    public ObjectFactory getObjects() {
        return project.getObjects();
    }

    @Override
    public ProjectLayout getLayout() {
        return project.getLayout();
    }

    @Override
    public File mkdir(Object path) {
        return project.mkdir(path);
    }

    @Override
    public boolean delete(Object... paths) {
        return project.delete(paths);
    }

    @Override
    public WorkResult delete(Action<? super DeleteSpec> action) {
        return project.delete(action);
    }

    @Override
    public ExecResult javaexec(Closure closure) {
        return project.javaexec(closure);
    }

    @Override
    public ExecResult javaexec(Action<? super JavaExecSpec> action) {
        return project.javaexec(action);
    }

    @Override
    public ExecResult exec(Closure closure) {
        return project.exec(closure);
    }

    @Override
    public ExecResult exec(Action<? super ExecSpec> action) {
        return project.exec(action);
    }

    @Override
    public String absoluteProjectPath(String path) {
        return project.absoluteProjectPath(path);
    }

    @Override
    public String relativeProjectPath(String path) {
        return project.relativeProjectPath(path);
    }

    @Override
    public AntBuilder getAnt() {
        return project.getAnt();
    }

    @Override
    public AntBuilder createAntBuilder() {
        return project.createAntBuilder();
    }

    @Override
    public AntBuilder ant(Closure configureClosure) {
        return project.ant(configureClosure);
    }

    @Override
    public AntBuilder ant(Action<? super AntBuilder> configureAction) {
        return project.ant(configureAction);
    }

    @Override
    public ConfigurationContainer getConfigurations() {
        return project.getConfigurations();
    }

    @Override
    public void configurations(Closure configureClosure) {
        project.configurations(configureClosure);
    }

    @Override
    public ArtifactHandler getArtifacts() {
        return project.getArtifacts();
    }

    @Override
    public void artifacts(Closure configureClosure) {
        project.artifacts(configureClosure);
    }

    @Override
    public void artifacts(Action<? super ArtifactHandler> configureAction) {
        project.artifacts(configureAction);
    }

    @Override
    public Convention getConvention() {
        return project.getConvention();
    }

    @Override
    public int depthCompare(Project otherProject) {
        return project.depthCompare(otherProject);
    }

    @Override
    public int getDepth() {
        return project.getDepth();
    }

    @Override
    public TaskContainer getTasks() {
        return requireTasks();
    }

    @Override
    public void subprojects(Action<? super Project> action) {
        project.subprojects(action);
    }

    @Override
    public void subprojects(Closure configureClosure) {
        project.subprojects(configureClosure);
    }

    @Override
    public void allprojects(Action<? super Project> action) {
        project.allprojects(action);
    }

    @Override
    public void allprojects(Closure configureClosure) {
        project.allprojects(configureClosure);
    }

    @Override
    public void beforeEvaluate(Action<? super Project> action) {
        project.beforeEvaluate(action);
    }

    @Override
    public void afterEvaluate(Action<? super Project> action) {
        project.afterEvaluate(action);
    }

    @Override
    public void beforeEvaluate(Closure closure) {
        project.beforeEvaluate(closure);
    }

    @Override
    public void afterEvaluate(Closure closure) {
        project.afterEvaluate(closure);
    }

    @Override
    public boolean hasProperty(String propertyName) {
        return project.hasProperty(propertyName);
    }

    @Override
    public Map<String, ?> getProperties() {
        return project.getProperties();
    }

    @Override
    @Nullable
    public Object property(String propertyName) throws MissingPropertyException {
        return project.property(propertyName);
    }

    @Override
    @Nullable
    public Object findProperty(String propertyName) {
        return project.findProperty(propertyName);
    }

    @Override
    public Logger getLogger() {
        return project.getLogger();
    }

    @Override
    public Gradle getGradle() {
        return project.getGradle();
    }

    @Override
    public LoggingManager getLogging() {
        return project.getLogging();
    }

    @Override
    public Object configure(Object object, Closure configureClosure) {
        return project.configure(object, configureClosure);
    }

    @Override
    public Iterable<?> configure(Iterable<?> objects, Closure configureClosure) {
        return project.configure(objects, configureClosure);
    }

    @Override
    public <T> Iterable<T> configure(Iterable<T> objects, Action<? super T> configureAction) {
        return project.configure(objects, configureAction);
    }

    @Override
    public RepositoryHandler getRepositories() {
        return project.getRepositories();
    }

    @Override
    public void repositories(Closure configureClosure) {
        project.repositories(configureClosure);
    }

    @Override
    public DependencyHandler getDependencies() {
        return project.getDependencies();
    }

    @Override
    public void dependencies(Closure configureClosure) {
        project.dependencies(configureClosure);
    }

    @Override
    public ScriptHandler getBuildscript() {
        return project.getBuildscript();
    }

    @Override
    public void buildscript(Closure configureClosure) {
        project.buildscript(configureClosure);
    }

    @Override
    public WorkResult copy(Closure closure) {
        return project.copy(closure);
    }

    @Override
    public WorkResult copy(Action<? super CopySpec> action) {
        return project.copy(action);
    }

    @Override
    public CopySpec copySpec(Closure closure) {
        return project.copySpec(closure);
    }

    @Override
    public CopySpec copySpec(Action<? super CopySpec> action) {
        return project.copySpec(action);
    }

    @Override
    public CopySpec copySpec() {
        return project.copySpec();
    }

    @Override
    public WorkResult sync(Action<? super CopySpec> action) {
        return project.sync(action);
    }

    @Override
    public ProjectState getState() {
        return project.getState();
    }

    @Override
    public <T> NamedDomainObjectContainer<T> container(Class<T> type) {
        return project.container(type);
    }

    @Override
    public <T> NamedDomainObjectContainer<T> container(Class<T> type, NamedDomainObjectFactory<T> factory) {
        return project.container(type, factory);
    }

    @Override
    public <T> NamedDomainObjectContainer<T> container(Class<T> type, Closure factoryClosure) {
        return project.container(type, factoryClosure);
    }

    @Override
    public ExtensionContainer getExtensions() {
        return project.getExtensions();
    }

    @Override
    public ResourceHandler getResources() {
        return project.getResources();
    }

    @Override
    public SoftwareComponentContainer getComponents() {
        return project.getComponents();
    }

    @Override
    public InputNormalizationHandler getNormalization() {
        return project.getNormalization();
    }

    @Override
    public void normalization(Action<? super InputNormalizationHandler> configuration) {
        project.normalization(configuration);
    }

    @Override
    public void dependencyLocking(Action<? super DependencyLockingHandler> configuration) {
        project.dependencyLocking(configuration);
    }

    @Override
    public DependencyLockingHandler getDependencyLocking() {
        return project.getDependencyLocking();
    }

    @Override
    public int compareTo(@NotNull Project project) {
        return this.project.compareTo(project);
    }

    @Override
    public PluginContainer getPlugins() {
        return project.getPlugins();
    }

    @Override
    public void apply(Closure closure) {
        project.apply(closure);
    }

    @Override
    public void apply(Action<? super ObjectConfigurationAction> action) {
        project.apply(action);
    }

    @Override
    public void apply(Map<String, ?> options) {
        project.apply(options);
    }

    @Override
    public PluginManager getPluginManager() {
        return project.getPluginManager();
    }
}
