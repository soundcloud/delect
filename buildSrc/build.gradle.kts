group = "com.soundcloud.delect"
version = "0.1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

plugins {
    `kotlin-dsl`
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "0.10.1"
    id("org.jmailen.kotlinter") version "1.25.2"
}

dependencies {
    compileOnly(gradleApi())

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
    testImplementation("com.google.truth:truth:0.42")
}

pluginBundle {
    website = "https://github.com/soundcloud/delect"
    vcsUrl = "https://github.com/soundcloud/delect"
    tags = listOf("dagger", "di", "delect", "reflect")

    mavenCoordinates {
        artifactId = "delect"
        groupId = group
    }
}

gradlePlugin {
    plugins {
        create("delect") {
            id = group.toString()
            displayName = "Delect"
            description = "The Gradle Plugin for Dagger Reflect"
            implementationClass = "com.soundcloud.reflect.DaggerReflectPlugin"
        }
    }
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
