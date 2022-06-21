package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_Yarn : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "~ [DEPRECATED] Yarn"

    params {
        text("MAVEN_MODULES", ":ignite-yarn", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteYarnTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 120
    }
})
