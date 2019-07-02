package com.soundcloud.reflect

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.util.VersionNumber

class DaggerReflectPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("delect", DelectExtension::class.java)

        if (!shouldActivateDaggerReflect(target, extension.useReflectForASBuilds)) {
            // we don't do anything if we haven't been invoked from the ide.
            return
        }

        target.logger.warn("Using Dagger Reflect")

        target.allprojects {
            configurations.all config@{
                dependencies.all {
                    // If we depend on the dagger runtime, also add the dagger reflect runtime.
                    if (group == daggerGroupId && name == "dagger") {
                        dependencies {
                            add(this@config.name, "$reflectDaggerGroupId:dagger-reflect:${extension.daggerReflectVersion}")
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
                            with(module("$reflectDaggerGroupId:dagger-reflect-compiler:${extension.daggerReflectVersion}"))
                            because("We want to build faster.")
                        }

                        substitute(module("$daggerGroupId:dagger-android-processor"))
                            .with(module("$reflectDaggerGroupId:dagger-reflect-compiler:${extension.daggerReflectVersion}"))
                    }
                }
            }
        }
    }

    private fun shouldActivateDaggerReflect(target: Project, useReflectForIdeBuilds: Boolean): Boolean {
        return (useReflectForIdeBuilds && target.properties.containsKey("android.injected.invoked.from.ide") || (target.properties.containsKey("dagger.reflect") && target.properties["dagger.reflect"] == "true"))
    }

    companion object {
        const val daggerGroupId = "com.google.dagger"
        const val reflectDaggerGroupId = "com.jakewharton.dagger"
        val minSupportedDaggerVersion = VersionNumber.parse("2.22")
    }
}
