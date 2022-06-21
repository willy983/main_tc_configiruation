package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_Kubernetes : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Kubernetes"

    params {
        text("MAVEN_MODULES", ":ignite-kubernetes", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteKubernetesTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 10
    }
})
