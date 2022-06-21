package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_OpenCensusNew : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Open Census"

    params {
        text("MAVEN_MODULES", ":ignite-opencensus", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteOpenCensusSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 30
    }
})
