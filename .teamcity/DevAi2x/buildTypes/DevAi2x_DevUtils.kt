package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_DevUtils : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Dev Utils"

    params {
        text("MAVEN_MODULES", ":ignite-dev-utils", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "DevUtilsTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 30
    }
})
