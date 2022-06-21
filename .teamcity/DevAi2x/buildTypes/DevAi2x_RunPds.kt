package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object DevAi2x_RunPds : BuildType({
    name = "-> Run :: PDS"
    description = "Dummy build for run all build in project by one click"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        showDependenciesChanges = true
    }

    triggers {
        vcs {
            enabled = false
            branchFilter = "+:refs/pull/548/head"
            watchChangesInDependencies = true
        }
        schedule {
            enabled = false
            schedulingPolicy = daily {
                hour = 4
            }
            branchFilter = "+:<default>"
            triggerBuild = always()
            withPendingChangesOnly = false
            param("revisionRuleBuildBranch", "<default>")

            enforceCleanCheckout = true
        }
        schedule {
            enabled = false
            schedulingPolicy = daily {
                hour = 6
            }
            branchFilter = "+:<default>"
            triggerBuild = always()
            withPendingChangesOnly = false
            param("revisionRuleBuildBranch", "<default>")

            enforceCleanCheckout = true
        }
    }

    dependencies {
        snapshot(DevAi2x_Pds1) {
        }
        snapshot(DevAi2x_Pds2) {
        }
        snapshot(DevAi2x_Pds3) {
        }
        snapshot(DevAi2x_Pds4) {
        }
        snapshot(DevAi2x_PdsCompatibility) {
        }
        snapshot(DevAi2x_PdsDirectIo1) {
        }
        snapshot(DevAi2x_PdsDirectIo2) {
        }
        snapshot(DevAi2x_PdsIndexing) {
        }
        snapshot(DevAi2x_PdsUnitTests) {
        }
    }
})
