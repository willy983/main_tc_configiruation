package IgniteThinClients

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("IgniteThinClients")
    name = "[Apache Ignite: Thin Clients]"

    subProject(IgniteThinClients_Tests.Project)
    subProject(IgniteThinClients_Releases.Project)
})
