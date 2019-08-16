package com.soundcloud.reflect

import com.google.common.truth.Truth.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class DaggerReflectPluginIntegrationTest {
    @get:Rule
    val testProjectDir = TemporaryFolder()

    @Test
    fun testPluginWithoutDaggerReflect() {
        val fixtureName = "java-module"
        writePluginBuildGradle()

        writeSettingsGradle(fixtureName)

        copyProjectFixture(fixtureName)

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath()
            .withArguments("run")
            .build()

        assertThat(result.output).contains("dagger reflect class not available")
    }
    @Test
    fun testWithDaggerReflect() {
        val fixtureName = "java-module"
        writePluginBuildGradle()

        writeSettingsGradle(fixtureName)

        copyProjectFixture(fixtureName)

        setDaggerReflectEnabled()
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath()
            .withArguments("run")
            .build()

        assertThat(result.output).contains("dagger reflect class is available")
    }

    private fun writePluginBuildGradle() {
        testProjectDir.newFile("build.gradle").writeText("""
            plugins {
                id 'com.soundcloud.delect'
            }
        """.trimIndent())
    }

    private fun writeSettingsGradle(moduleName: String) {
        testProjectDir.newFile("settings.gradle").writeText("""
            include '$moduleName'
        """.trimIndent())
    }

    private fun setDaggerReflectEnabled() {
        testProjectDir.newFile("gradle.properties").writeText("dagger.reflect=true")
    }

    private fun copyProjectFixture(fixtureName: String) {
        File("src/test/fixtures/$fixtureName").copyRecursively(testProjectDir.newFile(fixtureName), true)
    }
}