package ignite3_Test_IntegrationTests

import ignite3_Test_IntegrationTests.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("ignite3_Test_IntegrationTests")
    name = "[Integration Tests]"

    buildType(ignite3_Test_IntegrationTests_ModuleMetastorageClient)
    buildType(ignite3_Test_IntegrationTests_ModuleNetwork)
    buildType(ignite3_Test_IntegrationTests_ModuleRaft)
    buildType(ignite3_Test_IntegrationTests_ModuleExamples)
    buildType(ignite3_Test_IntegrationTests_ModuleVault)
    buildType(ignite3_Test_IntegrationTests_ModuleClusterManagement)
    buildType(ignite3_Test_IntegrationTests_ModuleTable)
    buildType(ignite3_Test_IntegrationTests_RunAllOther)
    buildType(ignite3_Test_IntegrationTests_ModuleCli)
    buildType(ignite3_Test_IntegrationTests_ModuleClientHandler)
    buildType(ignite3_Test_IntegrationTests_ModulePageMemory)
    buildType(ignite3_Test_IntegrationTests_ModuleConfigurationAnnotationProcessor)
    buildType(ignite3_Test_IntegrationTests_ModuleRunner)
})
