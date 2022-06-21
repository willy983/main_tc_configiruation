package ignite3_Test.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule

object ignite3_Test_RunTestsWindows : BuildType({
    name = "-> Run :: Tests (Windows)"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        showDependenciesChanges = true
    }

    triggers {
        schedule {
            schedulingPolicy = daily {
                hour = 0
            }
            branchFilter = "+:<default>"
            triggerBuild = always()
            withPendingChangesOnly = false

            enforceCleanCheckoutForDependencies = true
        }
    }

    dependencies {
        snapshot(ignite3_Test_WindowsTests.buildTypes.ignite3_Test_WindowsTests_IntegrationTests) {
        }
        snapshot(ignite3_Test_WindowsTests.buildTypes.ignite3_Test_WindowsTests_UnitTests) {
        }
    }
})
