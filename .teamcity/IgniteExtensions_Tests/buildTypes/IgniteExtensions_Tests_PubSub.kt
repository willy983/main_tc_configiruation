package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_PubSub : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Pub Sub"

    params {
        text("DIR_EXTENSION", "pub-sub-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
