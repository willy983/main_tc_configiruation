package ignite3_Release_Build

import ignite3_Release_Build.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("ignite3_Release_Build")
    name = "[Build]"

    buildType(ignite3_Release_Build_JavaBinariesDocs)
    buildType(ignite3_Release_Build_RPM)
    buildType(ignite3_Release_Build_DEB)
    buildType(ignite3_Release_Build_DotnetBinariesDocs)
    buildType(ignite3_Release_Build_Configure)

    cleanup {
        baseRule {
            all(days = 1)
            history(days = 1)
            artifacts(days = 1, artifactPatterns = """
                +:**/*
                +:.teamcity/**
            """.trimIndent())
            preventDependencyCleanup = false
        }
    }
})
