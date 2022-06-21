package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_Flume : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Flume"

    params {
        text("DIR_EXTENSION", "flume-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
