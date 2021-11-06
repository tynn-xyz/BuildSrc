BuildSrc
========
[![Build][build-badge]][build]

Convenience build tool extensions for _Gradle_ - _Kotlin_ and _Android_

The plugins are available in the [Gradle Plugin Portal] and support the
_plugins DSL_.


Adaptive Plugins
----------------

A collection of plugins patching minor issues of other plugins. These are
identified by their _id_ only. Thus no dependencies are defined or required.

### Fix IDEA Plugin
[![Plugin][idea-badge]][idea]

Fix minor issues within IDEA regarding source sets of Kotlin and Test Fixtures.

    id 'xyz.tynn.idea.fix' version 'x.y.z'

#### `kotlin-android`

Include all _Kotlin_ source directories into the _Java_ source sets for
_Android_ modules. A manual [configuration for _Android Studio_][KAS] is
unnecessary.

Just remove lines like the following

    main.java.srcDirs += 'src/main/kotlin'
    sourceSets["main"].java.srcDir("src/main/kotlin")

#### `java-test-fixtures`

The `testFixtures` source sets are added as test source directories and to the
`PROVIDED` scope.


Elements Plugins
----------------

A collection of plugins to help publishing _Java_ Library projects with
_Gradle_ metadata.

### Jvm

#### Jvm Library KDoc
[![Plugin][kdoc-jvm-badge]][kdoc-jvm]

Provides a _kdocElements_ configuration and a _kdocJar_ task to create _Java_
and _Kotlin_ variant KDoc artifact. This artifact is added to the _java_
component when requested with `withKdocJar`. This plugin uses [dokka] and
requires it in the build classpath only.

    id 'org.jetbrains.dokka' version '1.5.31'
    id 'xyz.tynn.jvm.kdoc' version 'x.y.z'

##### Usage

    java {
        withJavadocJar()
        withKdocJar()
        withSourcesJar()
    }

    publishing {
        publications {
            jvm(MavenPublication) {
                from components.java
            }
        }
    }


Publishing Plugins
------------------

A collection of plugins to help publishing _Android_ Library projects with
_Gradle_ metadata.

### Android

A collection of plugins utilising the [Support for the Maven Publish plugin]
available from version _3.6_ of the _Android Gradle_ plugin.

#### Android Library Maven
[![Plugin][maven-badge]][maven]

Provides publications for all release variant components provided with the
[Support for the Maven Publish plugin].

    id 'xyz.tynn.android.maven' version 'x.y.z'

The artifact id of each publication contains the product flavors in form of
`project.name-flavor1.name-flavor2.name`. 

A release component and publication is provided when built with Gradle 6.0 or
later. The publication contains the module meta data to link product flavors to
their publications. The product flavor dimensions always contain a namespace
defaulting to the group of the module.

##### Configuration and Publication naming

 * release
 * *variantName*
 * *variantName*MetaPublication

#### Android Library Javadoc
[![Plugin][javadoc-badge]][javadoc]

Provides configurations and tasks for _Java_ variant javadoc artifacts.
These artifacts are added to the components provided with the
[Support for the Maven Publish plugin].

    id 'xyz.tynn.android.javadoc' version 'x.y.z'

##### Task and Configuration naming

 * :*variantName*Javadoc
 * :*variantName*JavadocJar
 * *variantName*JavadocPublication
 * *variantName*AllJavadocPublication

#### Android Library KDoc
[![Plugin][kdoc-badge]][kdoc]

Provides configurations and tasks for _Java_  and _Kotlin_ variant KDoc
artifacts. These artifacts are added to the components provided with the
[Support for the Maven Publish plugin] from version 3.6 of the _Android_
_Gradle_ plugin. This plugin uses [dokka] and requires it in the build
classpath only.

    id 'org.jetbrains.dokka' version '1.5.31' apply false
    id 'xyz.tynn.android.kdoc' version 'x.y.z'

##### Task and Configuration naming

 * :*variantName*Kdoc
 * :*variantName*KdocJar
 * *variantName*KdocPublication
 * *variantName*AllKdocPublication

#### Android Library Sources
[![Plugin][sources-badge]][sources]

Provides configurations and tasks for _Java_ and _Kotlin_ variant sources
artifacts. These artifacts are added to the components provided with the
[Support for the Maven Publish plugin]

    id 'xyz.tynn.android.sources' version 'x.y.z'

##### Task and Configuration naming

 * :*variantName*SourcesJar
 * *variantName*SourcesPublication
 * *variantName*AllSourcesPublication


License
-------

    Copyright (C) 2019-2021 Christian Schmitz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


  [dokka]: https://github.com/Kotlin/dokka
  [KAS]: https://kotlinlang.org/docs/reference/using-gradle.html#android-studio
  [Gradle Plugin Portal]: https://plugins.gradle.org/
  [Support for the Maven Publish plugin]: https://developer.android.com/studio/preview/features#maven-publish

  [idea]: https://plugins.gradle.org/plugin/xyz.tynn.idea.fix
  [idea-badge]: https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/xyz/tynn/idea/fix/xyz.tynn.idea.fix.gradle.plugin/maven-metadata.xml?label=Plugin&logo=gradle
  [javadoc]: https://plugins.gradle.org/plugin/xyz.tynn.android.javadoc
  [javadoc-badge]: https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/xyz/tynn/android/javadoc/xyz.tynn.android.javadoc.gradle.plugin/maven-metadata.xml?label=Plugin&logo=gradle
  [kdoc]: https://plugins.gradle.org/plugin/xyz.tynn.android.kdoc
  [kdoc-badge]: https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/xyz/tynn/android/kdoc/xyz.tynn.android.kdoc.gradle.plugin/maven-metadata.xml?label=Plugin&logo=gradle
  [kdoc-jvm]: https://plugins.gradle.org/plugin/xyz.tynn.jvm.kdoc
  [kdoc-jvm-badge]: https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/xyz/tynn/jvm/kdoc/xyz.tynn.jvm.kdoc.gradle.plugin/maven-metadata.xml?label=Plugin&logo=gradle
  [maven]: https://plugins.gradle.org/plugin/xyz.tynn.android.maven
  [maven-badge]: https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/xyz/tynn/android/maven/xyz.tynn.android.maven.gradle.plugin/maven-metadata.xml?label=Plugin&logo=gradle
  [sources]: https://plugins.gradle.org/plugin/xyz.tynn.android.sources
  [sources-badge]: https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/xyz/tynn/android/sources/xyz.tynn.android.sources.gradle.plugin/maven-metadata.xml?label=Plugin&logo=gradle
  [build]: https://github.com/tynn-xyz/BuildSrc/actions
  [build-badge]: https://img.shields.io/github/workflow/status/tynn-xyz/BuildSrc/Build?label=Build&logo=github&logoColor=F5F5F5
