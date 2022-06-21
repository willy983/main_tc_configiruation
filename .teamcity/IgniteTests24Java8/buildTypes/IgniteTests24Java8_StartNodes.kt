package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_StartNodes : BuildType({
    templates(IgniteTests24Java8_RunTestsJava, IgniteTests24Java8_RunNodeLibs)
    name = "Start Nodes"

    params {
        text("MAVEN_MODULES", ":ignite-ssh", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.test.ssh.password", "teamcity", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteStartStopRestartTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.test.ssh.username", "teamcity", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 30
    }
})
