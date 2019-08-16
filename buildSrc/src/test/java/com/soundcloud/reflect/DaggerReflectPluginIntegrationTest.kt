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
    fun `test plugin without dagger reflect enabled`() {
        setupJavaModuleTextFixtureAndDelectPlugin()

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath()
            .withArguments("run")
            .build()

        assertThat(result.output).contains("dagger reflect class not available")
    }

    @Test
    fun `test plugin with dagger reflect enabled`() {
        setupJavaModuleTextFixtureAndDelectPlugin()

        enableDaggerReflect()
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath()
            .withArguments("run")
            .build()

        assertThat(result.output).contains("dagger reflect class is available")
    }

    private fun setupJavaModuleTextFixtureAndDelectPlugin() {
        val fixtureName = "java-module"
        writePluginBuildGradle()

        writeSettingsGradle(fixtureName)

        copyProjectFixture(fixtureName)
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

    private fun enableDaggerReflect() {
        testProjectDir.newFile("gradle.properties").writeText("dagger.reflect=true")
    }

    private fun copyProjectFixture(fixtureName: String) {
        File("src/test/fixtures/$fixtureName").copyRecursively(testProjectDir.newFile(fixtureName), true)
    }
}