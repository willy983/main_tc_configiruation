package IgniteThinClients_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteThinClients_Tests_RunAllTests : BuildType({
    name = "--> Run :: All Tests"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        showDependenciesChanges = true
    }

    dependencies {
        snapshot(IgniteThinClients_Tests_ThinClientNodeJs) {
        }
        snapshot(IgniteThinClients_Tests_ThinClientPhp) {
        }
        snapshot(IgniteThinClients_Tests_ThinClientPython) {
        }
    }
})
