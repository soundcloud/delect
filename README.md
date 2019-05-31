# Delect - The Gradle Plugin for Dagger Reflect

1. Add this to your project's root build.gradle:
```
apply plugin: 'com.soundcloud.delect'
```
2. Add the runtime retention policy to all `@Qualifier` annotations:
```
@Retention(RUNTIME)
```

2. Build the app as normal and skip all the dagger annotation procesing!
