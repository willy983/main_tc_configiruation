package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_GeospatialIndexing : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "~[DEPRECATED] Geospatial Indexing"

    params {
        text("MAVEN_MODULES", ":ignite-geospatial", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "GeoSpatialIndexingTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 15
    }
})
