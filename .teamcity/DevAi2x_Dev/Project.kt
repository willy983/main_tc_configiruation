package DevAi2x_Dev

import DevAi2x_Dev.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("DevAi2x_Dev")
    name = "[DEV]"

    buildType(DevAi2x_Dev_SqlTest)
})
