package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_Storm : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Storm"

    params {
        text("DIR_EXTENSION", "storm-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
