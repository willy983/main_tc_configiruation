package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_ZeroMQ : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "ZeroMQ"

    params {
        text("DIR_EXTENSION", "zeromq-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
