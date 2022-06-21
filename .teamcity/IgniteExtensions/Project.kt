package IgniteExtensions

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("IgniteExtensions")
    name = "[Apache Ignite: Extensions]"

    subProject(IgniteExtensions_Tests.Project)
})
