package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_CalciteSql : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Calcite SQL"

    params {
        text("MAVEN_MODULES", ":ignite-calcite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteCalciteTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 120
    }
})
