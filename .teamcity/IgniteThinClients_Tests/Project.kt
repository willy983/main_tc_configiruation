package IgniteThinClients_Tests

import IgniteThinClients_Tests.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("IgniteThinClients_Tests")
    name = "[TESTS]"

    buildType(IgniteThinClients_Tests_RunAllTests)
    buildType(IgniteThinClients_Tests_Build)
    buildType(IgniteThinClients_Tests_ThinClientNodeJs)
    buildType(IgniteThinClients_Tests_ThinClientPython)
    buildType(IgniteThinClients_Tests_ThinClientPythonAllPythons)
    buildType(IgniteThinClients_Tests_ThinClientPhp)

    params {
        text("reverse.dep.*.OVERRIDDEN_BRANCH", "master", label = "Branch", description = "Overridden branch for Apache Ignite repository", allowEmpty = false)
    }
})
