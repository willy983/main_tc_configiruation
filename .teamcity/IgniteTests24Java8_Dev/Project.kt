package IgniteTests24Java8_Dev

import IgniteTests24Java8_Dev.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("IgniteTests24Java8_Dev")
    name = "[DEV]"

    buildType(IgniteTests24Java8_Dev_SqlTest)
})
