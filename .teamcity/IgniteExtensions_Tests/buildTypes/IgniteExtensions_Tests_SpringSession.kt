package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_SpringSession : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Spring Session"

    params {
        text("DIR_EXTENSION", "spring-session-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
