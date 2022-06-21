package IgniteThinClients_Releases

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("IgniteThinClients_Releases")
    name = "[RELEASES]"

    subProject(IgniteThinClients_Releases_PythonThinClient.Project)
})
