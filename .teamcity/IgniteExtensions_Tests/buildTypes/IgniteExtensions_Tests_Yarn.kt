package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_Yarn : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Yarn"

    params {
        text("DIR_EXTENSION", "yarn-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
