package Releases_ApacheIgniteMain_ReleaseBuild

import Releases_ApacheIgniteMain_ReleaseBuild.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Releases_ApacheIgniteMain_ReleaseBuild")
    name = "[Release Build]"

    buildType(Releases_ApacheIgniteMain_ReleaseBuild_PrepareBuildOdbc)
})
