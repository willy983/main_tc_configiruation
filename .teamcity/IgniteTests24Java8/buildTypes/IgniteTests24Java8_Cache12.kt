package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_Cache12 : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Cache 12"

    params {
        text("TEST_SUITE", "IgniteCacheTestSuite12", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 90
    }
})
