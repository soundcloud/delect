package com.soundcloud.reflect

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.util.VersionNumber

class DaggerReflectPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (target != target.rootProject) {
            throw GradleException("Plugin must be applied to the root project")
        }

        val extension = target.extensions.create("delect", DelectExtension::class.java)

        target.afterEvaluate {
            if (shouldActivateDaggerReflect(target, extension)) {
                target.logger.warn("Using Dagger Reflect")
                activateDaggerReflect(target, extension)
            }
        }
    }

    private fun shouldActivateDaggerReflect(target: Project, extension: DelectExtension): Boolean =
        extension.enabled.getOrElse(isActivatedThroughGradleProperties(target))

    private fun isActivatedThroughGradleProperties(target: Project): Boolean {
        val provider = target.providers.gradleProperty("dagger.reflect").forUseAtConfigurationTime()
        return provider.isPresent && provider.get() == "true"
    }

    private fun activateDaggerReflect(target: Project, extension: DelectExtension) {
        target.allprojects {
            configurations.all config@{
                if (!extension.addReflectAnnotationProcessor) {
                    withDependencies {
                        removeIf { it.group == daggerGroupId && it.name == "dagger-compiler" }
                        removeIf { it.group == daggerGroupId && it.name == "dagger-android-processor" }
                    }
                }
                dependencies.all {
                    // If we depend on the dagger runtime, also add the dagger reflect runtime.
                    if (group == daggerGroupId && name == "dagger") {
                        dependencies {
                            add(
                                this@config.name,
                                "$reflectDaggerGroupId:dagger-reflect:${extension.daggerReflectVersion}"
                            )
                            if (extension.enableReflectLintCheck &&
                                (maxUnsupportedDaggerLintVersion < VersionNumber.parse(extension.daggerReflectVersion))
                            ) {
                                whenLintPluginAdded {
                                    add(
                                        "lintChecks",
                                        "$reflectDaggerGroupId:dagger-reflect-lint:${extension.daggerReflectVersion}"
                                    )
                                }
                            }
                        }
                    }
                }
                resolutionStrategy {
                    componentSelection {
                        withModule("$daggerGroupId:dagger") {
                            if (VersionNumber.parse(candidate.version) < minSupportedDaggerVersion) {
                                // We need to use at least 2.22 in order for certain dagger annotations to have runtime retention.
                                reject("Version must be $minSupportedDaggerVersion or higher.")
                            }
                        }
                    }
                    if (extension.addReflectAnnotationProcessor) {
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
    }

    private fun Project.whenLintPluginAdded(block: () -> Unit) {
        supportedLintPlugins.forEach { pluginId ->
            plugins.withId(pluginId) { block() }
        }
    }

    companion object {
        const val daggerGroupId = "com.google.dagger"
        const val reflectDaggerGroupId = "com.jakewharton.dagger"
        val minSupportedDaggerVersion = VersionNumber.parse("2.22")
        val maxUnsupportedDaggerLintVersion = VersionNumber.parse("0.1.0")
        val supportedLintPlugins = listOf("com.android.lint", "com.android.application", "com.android.library")
    }
}
