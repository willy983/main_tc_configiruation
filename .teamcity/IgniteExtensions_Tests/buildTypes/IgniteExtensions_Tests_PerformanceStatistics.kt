package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_PerformanceStatistics : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Performance Statistics"

    params {
        text("DIR_EXTENSION", "performance-statistics-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
