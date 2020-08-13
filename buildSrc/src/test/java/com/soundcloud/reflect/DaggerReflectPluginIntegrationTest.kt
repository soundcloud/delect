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

    @Test
    fun `test no code generation fails compile when referencing generated code`() {
        val fixtureName = "java-module"
        testProjectDir.newFile("build.gradle").writeText(
            """
            plugins {
                id 'com.soundcloud.delect'
            }
            delect {
                addReflectAnnotationProcessor = false
            }
            """.trimIndent()
        )

        writeSettingsGradle(fixtureName)
        copyProjectFixture(fixtureName)
        enableDaggerReflect()

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath()
            .withArguments("run")
            .buildAndFail()

        assertThat(result.output).contains(
            "error: cannot find symbol\n" +
                "        AppComponent appComponent = DaggerAppComponent.create();\n" +
                "                                    ^\n" +
                "  symbol:   variable DaggerAppComponent"
        )
    }

    @Test
    fun `test no code generation succeeds with full reflection`() {
        val fixtureName = "java-module-full-reflect"
        testProjectDir.newFile("build.gradle").writeText(
            """
            plugins {
                id 'com.soundcloud.delect'
            }
            delect {
                addReflectAnnotationProcessor = false
            }
            """.trimIndent()
        )

        writeSettingsGradle(fixtureName)
        copyProjectFixture(fixtureName)
        enableDaggerReflect()

        val result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath()
            .withArguments("run")
            .build()

        assertThat(result.output).contains("dagger reflect class is available")
    }

    @Test
    fun `is compatible with the configuration cache`() {
        setupJavaModuleTextFixtureAndDelectPlugin()

        enableDaggerReflect()
        val runner = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withPluginClasspath()
            .withGradleVersion("6.6")
            .withArguments("run", "--configuration-cache")

        val result = runner.build()
        assertThat(result.output).contains("SUCCESS")
        assertThat(result.output).contains("dagger reflect class is available")
        assertThat(result.output).contains("Configuration cache entry stored.")

        val resultTwo = runner.build()
        assertThat(resultTwo.output).contains("SUCCESS")
        assertThat(resultTwo.output).contains("dagger reflect class is available")
        assertThat(resultTwo.output).contains("Reusing configuration cache.")
    }

    private fun setupJavaModuleTextFixtureAndDelectPlugin() {
        val fixtureName = "java-module"
        writePluginBuildGradle()

        writeSettingsGradle(fixtureName)

        copyProjectFixture(fixtureName)
    }

    private fun writePluginBuildGradle() {
        testProjectDir.newFile("build.gradle").writeText(
            """
            plugins {
                id 'com.soundcloud.delect'
            }
            """.trimIndent()
        )
    }

    private fun writeSettingsGradle(moduleName: String) {
        testProjectDir.newFile("settings.gradle").writeText(
            """
            include '$moduleName'
            """.trimIndent()
        )
    }

    private fun enableDaggerReflect() {
        testProjectDir.newFile("gradle.properties").writeText("dagger.reflect=true")
    }

    private fun copyProjectFixture(fixtureName: String) {
        File("src/test/fixtures/$fixtureName").copyRecursively(testProjectDir.newFile(fixtureName), true)
    }
}
