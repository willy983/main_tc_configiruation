package ignite2_Release

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("ignite2_Release")
    name = "[Release]"

    subProject(Releases_ApacheIgniteNightly.Project)
    subProject(Releases_ApacheIgniteMain.Project)
})
