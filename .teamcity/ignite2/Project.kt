package ignite2

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("ignite2")
    name = "[Apache Ignite 2.x]"

    subProject(ignite2_Release.Project)
})
