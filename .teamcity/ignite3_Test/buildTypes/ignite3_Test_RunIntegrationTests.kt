package ignite3_Test.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object ignite3_Test_RunIntegrationTests : BuildType({
    name = "-> Run :: Integration Tests"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        showDependenciesChanges = true
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    dependencies {
        snapshot(ignite3_Test_IntegrationTests.buildTypes.ignite3_Test_IntegrationTests_ModuleCli) {
        }
        snapshot(ignite3_Test_IntegrationTests.buildTypes.ignite3_Test_IntegrationTests_ModuleClientHandler) {
        }
        snapshot(ignite3_Test_IntegrationTests.buildTypes.ignite3_Test_IntegrationTests_ModuleClusterManagement) {
        }
        snapshot(ignite3_Test_IntegrationTests.buildTypes.ignite3_Test_IntegrationTests_ModuleConfigurationAnnotationProcessor) {
        }
        snapshot(ignite3_Test_IntegrationTests.buildTypes.ignite3_Test_IntegrationTests_ModuleExamples) {
        }
        snapshot(ignite3_Test_IntegrationTests.buildTypes.ignite3_Test_IntegrationTests_ModuleMetastorageClient) {
        }
        snapshot(ignite3_Test_IntegrationTests.buildTypes.ignite3_Test_IntegrationTests_ModuleNetwork) {
        }
        snapshot(ignite3_Test_IntegrationTests.buildTypes.ignite3_Test_IntegrationTests_ModulePageMemory) {
        }
        snapshot(ignite3_Test_IntegrationTests.buildTypes.ignite3_Test_IntegrationTests_ModuleRaft) {
        }
        snapshot(ignite3_Test_IntegrationTests.buildTypes.ignite3_Test_IntegrationTests_ModuleRunner) {
        }
        snapshot(ignite3_Test_IntegrationTests.buildTypes.ignite3_Test_IntegrationTests_ModuleTable) {
        }
        snapshot(ignite3_Test_IntegrationTests.buildTypes.ignite3_Test_IntegrationTests_ModuleVault) {
        }
        snapshot(ignite3_Test_IntegrationTests.buildTypes.ignite3_Test_IntegrationTests_RunAllOther) {
        }
    }
})
