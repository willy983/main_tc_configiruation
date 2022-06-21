package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_Logging : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Logging"

    params {
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteLoggingSelfTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("system.FORK_COUNT_SET_TO_1", "true")
    }

    failureConditions {
        executionTimeoutMin = 20
    }
})
