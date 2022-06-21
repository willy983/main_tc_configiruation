package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_JavaClient : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Java Client"

    params {
        text("MAVEN_MODULES", ":ignite-clients,:ignite-rest-http", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteClientTestSuite,GridRestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("XMX", "8g")
    }

    failureConditions {
        executionTimeoutMin = 50
    }
})
