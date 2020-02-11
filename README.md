BuildSrc
========
[![Build][travis-badge]][travis]

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


Android Plugins
---------------

### Publishing

A collection of plugins to help publishing _Android_ Library projects with
_Gradle_ metadata.

#### Android Library Maven
[![Plugin][maven-badge]][maven]

Provides publications for all release variant components provided with the
[Support for the Maven Publish plugin] from version 3.6 of the _Android_
_Gradle_ plugin.

    id 'xyz.tynn.android.maven' version 'x.y.z'

##### Publication naming

 * *variantName*

#### Android Library Javadoc
[![Plugin][javadoc-badge]][javadoc]

Provides configurations and tasks for _Java_ variant javadoc artifacts.
These artifacts are added to the components provided with the
[Support for the Maven Publish plugin] from version 3.6 of the _Android_
_Gradle_ plugin.

    id 'xyz.tynn.android.javadoc' version 'x.y.z'

##### Task and Configuration naming

 * :*variantName*Javadoc
 * :*variantName*JavadocJar
 * *variantName*JavadocPublication
 * *variantName*AllJavadocPublication

#### Android Library Sources
[![Plugin][sources-badge]][sources]

Provides configurations and tasks for _Java_ and _Kotlin_ variant sources
artifacts. These artifacts are added to the components provided with the
[Support for the Maven Publish plugin] from version 3.6 of the _Android_
_Gradle_ plugin.

    id 'xyz.tynn.android.sources' version 'x.y.z'

##### Task and Configuration naming

 * :*variantName*SourcesJar
 * *variantName*SourcesPublication
 * *variantName*AllSourcesPublication


License
-------

    Copyright (C) 2019-2020 Christian Schmitz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


  [KAS]: https://kotlinlang.org/docs/reference/using-gradle.html#android-studio
  [Gradle Plugin Portal]: https://plugins.gradle.org/
  [Support for the Maven Publish plugin]: https://developer.android.com/studio/preview/features#maven-publish

  [idea]: https://plugins.gradle.org/plugin/xyz.tynn.idea.fix
  [idea-badge]: https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/xyz/tynn/idea/fix/xyz.tynn.idea.fix.gradle.plugin/maven-metadata.xml?label=Plugin&logo=gradle
  [javadoc]: https://plugins.gradle.org/plugin/xyz.tynn.android.javadoc
  [javadoc-badge]: https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/xyz/tynn/android/javadoc/xyz.tynn.android.javadoc.gradle.plugin/maven-metadata.xml?label=Plugin&logo=gradle
  [maven]: https://plugins.gradle.org/plugin/xyz.tynn.android.maven
  [maven-badge]: https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/xyz/tynn/android/maven/xyz.tynn.android.maven.gradle.plugin/maven-metadata.xml?label=Plugin&logo=gradle
  [sources]: https://plugins.gradle.org/plugin/xyz.tynn.android.sources
  [sources-badge]: https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/xyz/tynn/android/sources/xyz.tynn.android.sources.gradle.plugin/maven-metadata.xml?label=Plugin&logo=gradle
  [travis]: https://travis-ci.com/tynn-xyz/BuildSrc
  [travis-badge]: https://img.shields.io/travis/com/tynn-xyz/BuildSrc.svg?label=Build&logo=travis-ci&logoColor=white
