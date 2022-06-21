package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_Cdc : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "CDC"
    description = "CDC extension tests"

    params {
        text("DIR_EXTENSION", "cdc-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 150
    }
})
