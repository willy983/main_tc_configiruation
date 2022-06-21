package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_Camel : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Camel"

    params {
        text("DIR_EXTENSION", "camel-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
