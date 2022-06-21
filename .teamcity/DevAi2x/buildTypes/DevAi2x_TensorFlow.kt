package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_TensorFlow : BuildType({
    templates(DevAi2x_ExcludeTests, DevAi2x_RunTestSuitesJava)
    name = "TensorFlow"

    params {
        text("MAVEN_MODULES", ":ignite-tensorflow", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("SKIP_BUILD_CONDITION", "! -d tensorflow", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "TensorFlowTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 150
    }
})
