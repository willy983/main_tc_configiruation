package ignite3_Release

import ignite3_Release.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("ignite3_Release")
    name = "[Release]"

    buildType(ignite3_Release_Build_1)

    subProject(ignite3_Release_Build.Project)
})
