group = "com.soundcloud.delect"
version = "0.2.1"

repositories {
    google()
    mavenCentral()
}

plugins {
    `kotlin-dsl`
    id("java-gradle-plugin")
    id("org.jmailen.kotlinter") version "1.25.2"
    `maven-publish`
    signing
}

dependencies {
    compileOnly(gradleApi())

    testImplementation(gradleTestKit())
    testImplementation("junit:junit:4.12")
    testImplementation("com.google.truth:truth:0.42")
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

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

val isReleaseBuild : Boolean = !version.toString().endsWith("SNAPSHOT")

val sonatypeUsername : String? by project
val sonatypePassword : String? by project

publishing {
    repositories {
        repositories {
            maven {
                val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
                url = if (isReleaseBuild) releasesRepoUrl else snapshotsRepoUrl
                credentials {
                    username = sonatypeUsername
                    password = sonatypePassword
                }
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            pom {
                name.set("Delect")
                description.set("The Gradle Plugin for Dagger Reflect")
                url.set("https://github.com/soundcloud/delect")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("SoundCloud")
                        name.set("SoundCloud")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/soundcloud/delect.git")
                    developerConnection.set("scm:git:ssh://github.com/soundcloud/delect.git")
                    url.set("https://github.com/soundcloud/delect")
                }
            }
        }
    }
}

signing {
    setRequired(isReleaseBuild)
    sign(publishing.publications["mavenJava"])
}
