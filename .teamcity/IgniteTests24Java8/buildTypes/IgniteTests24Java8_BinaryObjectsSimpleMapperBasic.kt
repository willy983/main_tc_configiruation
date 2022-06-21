package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_BinaryObjectsSimpleMapperBasic : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
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
