package Releases_ApacheIgniteNightly

import Releases_ApacheIgniteNightly.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Releases_ApacheIgniteNightly")
    name = "[Nightly]"

    buildType(Releases_NightlyRelease_RunApacheIgniteNightlyRelease)
    buildType(Releases_NightlyRelease_ApacheIgniteNightlyReleaseAssembleDockerImage)
    buildType(Releases_NightlyRelease_ApacheIgniteNightlyReleaseBuildNetCpp)
    buildType(Releases_NightlyRelease_ApacheIgniteNightlyReleaseAssembleBinaries)
    buildType(Releases_NightlyRelease_ApacheIgniteNightlyReleasePrepare)
    buildType(Releases_NightlyRelease_ApacheIgniteNightlyRelease3AssemblePackages)
})
