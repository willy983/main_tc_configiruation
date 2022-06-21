package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_ClientNodes : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Client Nodes"

    params {
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("XMX", "4g")
        text("TEST_SUITE", "IgniteClientNodesTestSuite,IgniteClientReconnectTestSuite,GridJettySuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("XMS", "4g")
    }

    failureConditions {
        executionTimeoutMin = 60
    }
})
