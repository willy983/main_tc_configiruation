package Releases_ApacheIgniteMain

import Releases_ApacheIgniteMain.buildTypes.*
import Releases_ApacheIgniteMain.vcsRoots.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Releases_ApacheIgniteMain")
    name = "[Main]"

    vcsRoot(Releases_ApacheIgniteMain_GitBoxIgnite)

    buildType(ApacheIgniteReleaseJava8_PrepareVote4CheckRcLicensesChecksum)
    buildType(ApacheIgniteReleaseJava8_IgniteRelease72CheckFileConsistency)
    buildType(Releases_ApacheIgniteMain_ReleaseBuild_1)
    buildType(ApacheIgniteReleaseJava8_PrepareVote3BuildNuGetPackages)
    buildType(Releases_ApacheIgniteMain_AssembleDockerImages)

    subProject(Releases_ApacheIgniteMain_ReleaseBuild.Project)
})
