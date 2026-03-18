BuildSrc
========
[![Build][build-badge]][build]

###### Convenience build tool extensions for _Gradle_ - _Kotlin_ and _Android_
[Gradle Plugin Portal]


Convention Plugins
------------------

### _**Settings**_ convention

[![Plugin][convention-settings-badge]][convention-settings]

    plugins {
        id 'xyz.tynn.convention.settings' version "${xyzTynnConventionVersion}"
    }

The published base convention plugin which should be applied to the _Gradle_
settings.

* Applies `xyz.tynn.convention.catalog` to the settings
* Applies `xyz.tynn.convention.maven` to the settings

### _**Catalog**_ convention

    plugins {
        id 'xyz.tynn.convention.catalog'
    }

Configures a [Versions Catalog] named `convention` containing
the current convention version and all project level plugins.

### _**Maven**_ convention

    plugins {
        id 'xyz.tynn.convention.maven'
    }

Configures all `google()` _Maven_ repositories to only include
groups related to _Google_:

 * `android` and `android.*`
 * `androidx` and `androidx.*`
 * `com.android` and `com.android.*`
 * `com.crashlytics` and `com.crashlytics.*`
 * `com.google` and `com.google.*`
 * `org.chromium` and `org.chromium.*`
 * `org.multipaz` and `org.multipaz.*`

### _**Project**_ convention

[![Plugin][convention-project-badge]][convention-project]

    plugins {
        id 'xyz.tynn.convention.project' version 'x.y.z'
        alias convention.plugins.project
    }

The published base convention plugin which should be applied to the root
project.

 * Applies `xyz.tynn.convention.android` to all _Android_ application and
   library projects
 * Applies `xyz.tynn.convention.release` to all projects with `maven-publish`
 * Configures the [Versions Plugin] to use the current _Gradle_ release and
   _disallow release candidates as upgradable versions from stable versions_
 * Configures all `Test` tasks to log the full exception on failure
 * Configures the `wrapper` task to use the complete _Gradle_ distribution
   by default
 * Creates a `clean` task if none has been added after the evaluation

### _**BOM**_ convention

    plugins {
        id 'xyz.tynn.convention.bom'
        alias convention.plugins.bom
    }

Configures the `project` to collect all other published projects to create a
[Gradle BOM] to align the dependency versions.

### _Android_ convention

    plugins {
        id 'xyz.tynn.convention.android'
        alias convention.plugins.android
    }

Configures an _Android_ project and provides a marker plugin to simplify
further configurations.

    subprojects {
        pluginManager.withPlugin('xyz.tynn.convention.android') {
            android {
                ...
            }
        }
    }

#### Default configuration

 * Sets the namespace to `${group}.${project.name}`
 * Sets the compile and target SDK level to 36
 * Sets the minimum SDK level to 24 by default
   * Override with `android.defaults.sdk.min` in `gradle.properties`
 * Uses the _AndroidX_ test runner by default

#### App configuration

##### Release

Enables minification by default and uses the debug signing config for builds.

##### Debug

Configures a `.debug` application id suffix and `+debug` version name suffix.

#### Lint configuration

Configures _Lint_ to abort on error and treat warnings as errors.

#### _Kotlin_ explicit API

Uses the _Kotlin_ explicit API configuration for _Android_. Instead of adding
`-Xexplicit-api=strict` manually it is now possible to just use the _Kotlin_
DSL variants.

    kotlin {
        explicitApi()
    }

### Kotlin convention

    plugins {
        id 'xyz.tynn.convention.kotlin'
        alias convention.plugins.kotlin
    }

Configures a _Kotlin_ project and provides a marker plugin to simplify
further configurations. It adds an implementation dependency to the Stdlib
and a test implementation dependency Kotlin Test.

### Release convention

    plugins {
        id 'xyz.tynn.convention.release'
        alias convention.plugins.release
    }

Configures _Maven_ publishing of a release publication for the `project`.
The POM is setup with `project.name` and `PROJECT_URL` environment variable.

Optionally configures signing if the `SIGNING_KEY` and `SIGNING_PASSWORD`
environment variables are provided.

#### Supported project structures

 * `java`
 * `java-platform`
 * `version-catalog`
 * `com.android.library`


License
-------

    Copyright (C) 2019-2026 Christian Schmitz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


  [Gradle BOM]: https://docs.gradle.org/current/userguide/dependency_version_alignment.html
  [Gradle Plugin Portal]: https://plugins.gradle.org/search?term=xyz.tynn
  [Versions Catalog]: https://docs.gradle.org/current/userguide/version_catalogs.html
  [Versions Plugin]: https://plugins.gradle.org/search?term=xyz.tynn

  [build]: https://github.com/tynn-xyz/BuildSrc/actions
  [build-badge]: https://img.shields.io/github/actions/workflow/status/tynn-xyz/BuildSrc/build.yml?label=Build&logo=github&logoColor=F5F5F5
  [convention-project]: https://plugins.gradle.org/plugin/xyz.tynn.convention.project
  [convention-project-badge]: https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fxyz%2Ftynn%2Fconvention%2Fproject%2Fxyz.tynn.convention.project.gradle.plugin%2Fmaven-metadata.xml&label=Plugin&logo=gradle
  [convention-settings]: https://plugins.gradle.org/plugin/xyz.tynn.convention.settings
  [convention-settings-badge]: https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fxyz%2Ftynn%2Fconvention%2Fsettings%2Fxyz.tynn.convention.settings.gradle.plugin%2Fmaven-metadata.xml&label=Plugin&logo=gradle
