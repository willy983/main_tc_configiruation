package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_Flink : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Flink"

    params {
        text("DIR_EXTENSION", "flink-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
