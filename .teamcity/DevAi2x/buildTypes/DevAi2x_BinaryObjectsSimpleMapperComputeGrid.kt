package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_BinaryObjectsSimpleMapperComputeGrid : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Binary Objects (Simple Mapper Compute Grid)"

    params {
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteBinaryObjectsSimpleNameMapperComputeGridTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 60
    }
})
