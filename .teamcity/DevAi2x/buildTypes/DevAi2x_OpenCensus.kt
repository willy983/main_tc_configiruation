package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_OpenCensus : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Open Census"

    params {
        text("MAVEN_MODULES", ":ignite-opencensus", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteOpenCensusSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 30
    }
})
