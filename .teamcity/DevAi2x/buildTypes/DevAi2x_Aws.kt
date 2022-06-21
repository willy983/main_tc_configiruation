package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_Aws : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "AWS"

    params {
        text("MAVEN_MODULES", ":ignite-aws", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteS3TestSuite,IgniteElbTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 30
    }
})
