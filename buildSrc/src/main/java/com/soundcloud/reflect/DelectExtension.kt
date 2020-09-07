package com.soundcloud.reflect

open class DelectExtension {
    var daggerReflectVersion = "0.3.0"

    /**
     * Whether or not the plugin is enabled. If set, takes precedence over `gradle.properties` configuration
     */
    var enabled: Boolean? = null

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
