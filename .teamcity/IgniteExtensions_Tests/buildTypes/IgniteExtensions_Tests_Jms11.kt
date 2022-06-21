package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_Jms11 : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Jms11"

    params {
        text("DIR_EXTENSION", "jms11-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
