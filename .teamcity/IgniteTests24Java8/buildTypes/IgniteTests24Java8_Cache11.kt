package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_Cache11 : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Cache 11"

    params {
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("XMX", "4g", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("JVM_ARGS", """
            -XX:+UseG1GC
            -XX:+PerfDisableSharedMem
        """.trimIndent(), display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteCacheTestSuite11", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 180
    }
})
