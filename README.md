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


License
-------

    Copyright (C) 2019 Christian Schmitz

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

  [idea]: https://plugins.gradle.org/plugin/xyz.tynn.idea.fix
  [idea-badge]: https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/xyz/tynn/idea/fix/xyz.tynn.idea.fix.gradle.plugin/maven-metadata.xml?label=Plugin&logo=gradle
  [travis]: https://travis-ci.com/tynn-xyz/BuildSrc
  [travis-badge]: https://img.shields.io/travis/com/tynn-xyz/BuildSrc.svg?label=Build&logo=travis
