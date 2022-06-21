package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_ClientNodes : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Client Nodes"

    params {
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("XMX", "4g", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteClientNodesTestSuite,org.apache.ignite.testsuites.IgniteClientReconnectTestSuite,org.apache.ignite.internal.processors.rest.protocols.http.jetty.GridJettySuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("XMS", "4g", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 60
    }
})
