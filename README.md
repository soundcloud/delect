# Delect - The Gradle Plugin for Dagger Reflect

Delect automatically substitutes Dagger for Dagger Reflect for development builds.

[Dagger](https://github.com/google/dagger), a dependency injection framework for Java, can slow down compilation with its lengthy annotation processing and code generation.

[Dagger Reflect](https://github.com/jakewharton/dagger-reflect) uses the same API as Dagger but swaps the compile time annotation processing for runtime based reflection.

## Usage
1. Add the plugin to your project's root build.gradle:
```
buildscript {
  classpath 'com.soundcloud.delect:delect-plugin:0.1.0-SNAPSHOT'
}
apply plugin: 'com.soundcloud.delect'
```
2. Add the runtime retention policy to all `@Qualifier` annotations:
```
@Retention(RUNTIME)
```
3. Add `dagger.reflect=true` to `gradle.properties`.
  You can also add it to `~/.gradle/gradle.properties` so it doesn't need to be checked into the project.
4. Build -- and skip all that pesky code generation!


## Plugin Development

To publish to maven local:
```
./gradlew -b buildSrc/build.gradle.kts publishToMavenLocal
```
To publish:
```
./gradlew -b buildSrc/build.gradle.kts publish
```

## License

Copyright 2019 SoundCloud

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
