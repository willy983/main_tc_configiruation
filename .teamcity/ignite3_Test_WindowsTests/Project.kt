package ignite3_Test_WindowsTests

import ignite3_Test_WindowsTests.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("ignite3_Test_WindowsTests")
    name = "[Windows Tests]"

    buildType(ignite3_Test_WindowsTests_IntegrationTests)
    buildType(ignite3_Test_WindowsTests_UnitTests)
})
