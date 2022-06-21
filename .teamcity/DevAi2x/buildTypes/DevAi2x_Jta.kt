package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_Jta : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "JTA"

    params {
        text("MAVEN_MODULES", ":ignite-jta", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteJtaTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 20
    }
})
