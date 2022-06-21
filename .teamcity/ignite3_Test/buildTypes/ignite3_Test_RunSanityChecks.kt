package ignite3_Test.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule

object ignite3_Test_RunSanityChecks : BuildType({
    name = "-> Run :: Sanity Checks"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        showDependenciesChanges = true
    }

    triggers {
        schedule {
            schedulingPolicy = daily {
                hour = 6
            }
            branchFilter = "+:<default>"
            triggerBuild = always()
            withPendingChangesOnly = false
            enableQueueOptimization = false

            enforceCleanCheckoutForDependencies = true
        }
    }

    dependencies {
        snapshot(ignite3_Test_SanityChecks.buildTypes.ignite3_Test_SanityChecks_CodeStyle) {
        }
        snapshot(ignite3_Test_SanityChecks.buildTypes.ignite3_Test_SanityChecks_Inspections) {
        }
        snapshot(ignite3_Test_SanityChecks.buildTypes.ignite3_Test_SanityChecks_Javadoc) {
        }
        snapshot(ignite3_Test_SanityChecks.buildTypes.ignite3_Test_SanityChecks_LegacyApi) {
        }
        snapshot(ignite3_Test_SanityChecks.buildTypes.ignite3_Test_SanityChecks_LicensesHeaders) {
        }
        snapshot(ignite3_Test_SanityChecks.buildTypes.ignite3_Test_SanityChecks_Maven) {
        }
        snapshot(ignite3_Test_SanityChecks.buildTypes.ignite3_Test_SanityChecks_Pmd) {
        }
    }
})
