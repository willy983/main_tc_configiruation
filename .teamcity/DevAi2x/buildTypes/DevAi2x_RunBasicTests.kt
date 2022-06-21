package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object DevAi2x_RunBasicTests : BuildType({
    name = "--> Run :: Basic Tests"
    description = "Runs fastest test suites to get reply as fast as possible. Use -Run All for check changes"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        showDependenciesChanges = true
    }

    triggers {
        vcs {
            enabled = false
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_DEFAULT
            branchFilter = """
                +:<default>
                +:pull/3667/head
                +:ignite-2.5
            """.trimIndent()
        }
    }

    dependencies {
        snapshot(DevAi2x_Basic1) {
        }
        snapshot(DevAi2x_Basic3) {
        }
        snapshot(DevAi2x_CacheFullApi) {
        }
        snapshot(DevAi2x_ComputeGrid) {
        }
        snapshot(DevAi2x_InspectionsCore) {
        }
        snapshot(DevAi2x_PdsUnitTests) {
        }
        snapshot(DevAi2x_PlatformNet) {
        }
        snapshot(DevAi2x_PlatformNetCoreLinux) {
        }
        snapshot(DevAi2x_Security) {
        }
    }
})
