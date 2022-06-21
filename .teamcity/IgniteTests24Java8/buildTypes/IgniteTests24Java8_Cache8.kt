package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_Cache8 : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Cache 8"

    params {
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteCacheTestSuite8", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("XMX", "4g")
    }

    failureConditions {
        executionTimeoutMin = 210
    }
})
