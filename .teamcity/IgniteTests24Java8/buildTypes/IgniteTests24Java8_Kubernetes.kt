package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_Kubernetes : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Kubernetes"

    params {
        text("MAVEN_MODULES", ":ignite-kubernetes", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteKubernetesTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 20
    }
})
