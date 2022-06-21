package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_TopologyValidator : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Topology Validator"

    params {
        text("DIR_EXTENSION", "topology-validator-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
