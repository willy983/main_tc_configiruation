package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_Twitter : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Twitter"

    params {
        text("DIR_EXTENSION", "twitter-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
