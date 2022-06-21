package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_Cloud : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Cloud"

    params {
        text("DIR_EXTENSION", "cloud-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
