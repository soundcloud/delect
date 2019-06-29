package com.soundcloud.reflect

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories
import org.gradle.util.VersionNumber

class DaggerReflectPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create<DelectExtension>("delect")
        target.afterEvaluate {
            if (shouldActivateDaggerReflect(extension.useReflectForASBuilds)) {
                target.logger.warn("Using Dagger Reflect")
                activateDaggerReflect(extension.daggerReflectVersion)
            }
        }
    }

    private fun Project.activateDaggerReflect(daggerReflectVersion: String) {
        allprojects {

            // For now we need to add the snapshot repository because dagger-reflect isn't yet published in maven central.
            repositories {
                maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots")  }
            }

            configurations.all config@{
                dependencies.all {
                    // If we depend on the dagger runtime, also add the dagger reflect runtime.
                    if (group == daggerGroupId && name == "dagger") {
                        dependencies {
                            add(this@config.name, "$reflectDaggerGroupId:dagger-reflect:$daggerReflectVersion")
                        }
                    }
                }
                resolutionStrategy {
                    componentSelection {
                        withModule("$daggerGroupId:dagger") {
                            if (VersionNumber.parse(candidate.version) < minSupportedDaggerVersion) {
                                // We need to use at least 2.22 in order for certain dagger annotations to have runtime retention.
                                reject("Version must be 2.22 or higher.")
                            }
                        }
                    }
                    dependencySubstitution {
                        // Substitute dagger compiler for dagger reflect compiler.
                        substitute(
                            module("$daggerGroupId:dagger-compiler")
                        ).apply {
                            with(module("$reflectDaggerGroupId:dagger-reflect-compiler:$daggerReflectVersion"))
                            because("We want to build faster.")
                        }

                        substitute(module("$daggerGroupId:dagger-android-processor"))
                            .with(module("$reflectDaggerGroupId:dagger-reflect-compiler:$daggerReflectVersion"))
                    }
                }
            }
        }
    }

    private fun Project.shouldActivateDaggerReflect(useReflectForIdeBuilds: Boolean): Boolean {
        return (useReflectForIdeBuilds && properties.containsKey("android.injected.invoked.from.ide") ||
                (properties.containsKey("dagger.reflect") && properties["dagger.reflect"] == "true"))
    }

    companion object {
        const val daggerGroupId = "com.google.dagger"
        const val reflectDaggerGroupId = "com.jakewharton.dagger"
        val minSupportedDaggerVersion = VersionNumber.parse("2.22")
    }
}
