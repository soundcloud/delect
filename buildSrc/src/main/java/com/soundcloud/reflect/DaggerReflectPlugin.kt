package com.soundcloud.reflect

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories

class DaggerReflectPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (!shouldActivateDaggerReflect(target)) {
            // we don't do anything if we haven't been invoked from the ide.
            return
        }

        target.logger.warn("Using Dagger Reflect")

        target.allprojects {

            // For now we need to add the snapshot repository because dagger-reflect isn't yet published in maven central.
            repositories {
                maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
            }

            val extension = extensions.create("delect", DelectExtension::class.java)

            configurations.all {
                // First remove the dagger android processor.
                withDependencies {
                    removeIf { group == "com.google.dagger" && name == "dagger-android-processor" }
                }

                dependencies.all {
                    // If we depend on the dagger runtime, also add the dagger reflect runtime.
                    if (group == "com.google.dagger" && name == "dagger") {
                        dependencies {
                            add("implementation", "com.jakewharton.dagger:dagger-reflect:${extension.daggerReflectVersion}")
                        }
                    }
                }
                resolutionStrategy {
                    componentSelection {
                        withModule("com.google.dagger:dagger") {
                            // TODO check if version is 2.21 or less
                            // We need to use at least 2.22 in order for certain dagger annotations to have runtime retention.
                            if (candidate.version == "2.21") {
                                reject("Version must be 2.22 or higher.")
                            }
                        }
                    }
                    dependencySubstitution {
                        // Substitute dagger compiler for dagger reflect compiler.
                        substitute(
                            module("com.google.dagger:dagger-compiler")
                        ).apply {
                            with(module("com.jakewharton.dagger:dagger-reflect-compiler:${extension.daggerReflectVersion}"))
                            because("We want to build faster.")
                        }
                    }
                }
            }
        }
    }

    private fun shouldActivateDaggerReflect(target: Project): Boolean {
        return (target.properties.containsKey("android.injected.invoked.from.ide") || (target.properties.containsKey("dagger.reflect") && target.properties["dagger.reflect"] == "true"))
    }
}