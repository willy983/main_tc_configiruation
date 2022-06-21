package IgniteThinClients_Releases_PythonThinClient

import IgniteThinClients_Releases_PythonThinClient.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("IgniteThinClients_Releases_PythonThinClient")
    name = "Python Thin Client"

    buildType(Releases_ApacheIgnitePythonThinClient_ReleaseDeploy)
    buildType(IgniteThinClients_Releases_PythonThinClient_ReleaseBuild)
})
