package Releases_IgniteH2

import Releases_IgniteH2.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Releases_IgniteH2")
    name = "Ignite H2"

    buildType(Releases_IgniteH2_ReleaseBuild)
})
