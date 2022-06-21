package ignite3_Test

import ignite3_Test.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("ignite3_Test")
    name = "[Test]"

    buildType(ignite3_Test_RunNetTests)
    buildType(ignite3_Test_RunTestsWindows)
    buildType(ignite3_Test_RunIntegrationTests)
    buildType(ignite3_Test_RunSanityChecks)
    buildType(ignite3_Test_RunAllTests)
    buildType(ignite3_Test_RunUnitTests)

    template(ignite3_Test_IntegrationTests_1)

    subProject(ignite3_Test_IntegrationTests.Project)
    subProject(ignite3_Test_SanityChecks.Project)
    subProject(ignite3_Test_WindowsTests.Project)
})
