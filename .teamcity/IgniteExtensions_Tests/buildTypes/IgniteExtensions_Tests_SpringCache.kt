package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_SpringCache : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Spring Cache"

    params {
        text("DIR_EXTENSION", "spring-cache-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
