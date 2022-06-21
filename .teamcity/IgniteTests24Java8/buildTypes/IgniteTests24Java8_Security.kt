package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_Security : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Security"

    params {
        param("MAVEN_OPTS", "-DforkMode=always")
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("env.IGNITE_TEST_FEATURES_ENABLED", "true")
        param("JVM_ARGS", "-DIGNITE_MARSHAL_BUFFERS_RECHECK=1000")
        text("TEST_SUITE", "SecurityTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 120
    }
})
