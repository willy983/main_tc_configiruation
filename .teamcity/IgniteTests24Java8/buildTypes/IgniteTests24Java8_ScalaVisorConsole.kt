package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_ScalaVisorConsole : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Scala (Visor Console)"

    params {
        text("MAVEN_MODULES", ":ignite-visor-console", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "VisorConsoleSelfTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 20
    }
})
