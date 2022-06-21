package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_BinaryObjectsSimpleMapperBasic : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Binary Objects (Simple Mapper Basic)"

    params {
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteBinarySimpleNameMapperBasicTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("env.IGNITE_TEST_FEATURES_ENABLED", "true")
    }

    failureConditions {
        executionTimeoutMin = 120
    }
})
