package Releases

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Releases")
    name = "[RELEASES]"

    subProject(Releases_IgniteH2.Project)
})
