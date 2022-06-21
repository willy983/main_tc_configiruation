package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_ActivateDeactivateCluster : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Activate / Deactivate Cluster"

    params {
        text("JVM_ARGS", "-DIGNITE_MARSHAL_BUFFERS_RECHECK=1000", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteStandByClusterSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 120
    }

    requirements {
        doesNotMatch("teamcity.agent.name", "(^publicagent05.*${'$'})", "RQ_72")
    }
})
