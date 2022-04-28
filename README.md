⚠️ **Delect has been retired!**

After 2+ years of service it's time to retire Delect. We won’t be developing the plugin further and we won’t provide support but you’re welcome to keep using Delect if it works for you ✌️

# Delect - The Gradle Plugin for Dagger Reflect

Delect automatically substitutes Dagger for Dagger Reflect for faster local builds.

[Dagger](https://github.com/google/dagger), a dependency injection framework for Java, can slow down compilation with its lengthy annotation processing and code generation.

[Dagger Reflect](https://github.com/jakewharton/dagger-reflect) uses the same API as Dagger but swaps the compile time annotation processing for runtime based reflection.

## Usage
Delect uses the [partial reflection approach](https://github.com/jakewharton/dagger-reflect#usage).

1. Add the plugin to your project's root build.gradle:
```
buildscript {
  classpath 'com.soundcloud.delect:delect-plugin:0.3.0'
}
apply plugin: 'com.soundcloud.delect'
```
2. Make sure to use the `@Component.Builder` or `@Component.Factory` to create component instances.
3. Add the runtime retention policy to all `@Qualifier`, `@MapKey` and other custom Dagger annotations:
```
@Retention(RUNTIME)
```
4. Add `dagger.reflect=true` to `gradle.properties`.
  You can also add it to `~/.gradle/gradle.properties` so it doesn't need to be checked into the project.
5. Build -- and skip all that pesky code generation!

## Plugin Development

To publish to maven local:
```
./gradlew -b buildSrc/build.gradle.kts publishToMavenLocal
```
To publish:
```
./gradlew -b buildSrc/build.gradle.kts publishMavenJavaPublicationToMavenRepository
```

## Found more than one jar in the 'lintChecks' configuration.

This is a [bug in AGP](https://issuetracker.google.com/issues/141840950).
To disable Dagger Reflect lint checks:
```groovy
delect {
  enableReflectLintCheck = false
}
```

## License

```
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
```
