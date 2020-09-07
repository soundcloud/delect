package com.soundcloud.reflect

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

open class DelectExtension(factory: ObjectFactory) {
    var daggerReflectVersion = "0.3.0"

    /**
     * Whether or not the plugin is enabled. If set, takes precedence over `gradle.properties` configuration
     */
    val enabled: Property<Boolean> = factory.property(Boolean::class.java)

    /**
     * By default, we use the Reflect Annotation Processor to connect the App's code the the
     * Dagger Reflect runtime as outlined in the partial reflection approach:
     * https://github.com/jakewharton/dagger-reflect#partial-reflection
     *
     * Disable to use the full reflection approach. This requires changing the app's logic a bit.
     * https://github.com/jakewharton/dagger-reflect#full-reflection
     */
    var addReflectAnnotationProcessor = true

    var enableReflectLintCheck = true
}
