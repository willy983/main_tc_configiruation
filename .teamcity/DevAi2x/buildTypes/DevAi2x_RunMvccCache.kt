package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_RunMvccCache : BuildType({
    name = "-> Run :: MVCC Cache"
    description = "Dummy build for run all build in project by one click"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        showDependenciesChanges = true
    }

    dependencies {
        snapshot(DevAi2x_MvccCache1) {
        }
        snapshot(DevAi2x_MvccCache2) {
        }
        snapshot(DevAi2x_MvccCache3) {
        }
        snapshot(DevAi2x_MvccCache4) {
        }
        snapshot(DevAi2x_MvccCache5) {
        }
        snapshot(DevAi2x_MvccCache6) {
        }
        snapshot(DevAi2x_MvccCache7) {
        }
        snapshot(DevAi2x_MvccCache8) {
        }
        snapshot(DevAi2x_MvccCache9) {
        }
        snapshot(DevAi2x_MvccPds1) {
        }
        snapshot(DevAi2x_MvccPds2) {
        }
        snapshot(DevAi2x_MvccPds3) {
        }
        snapshot(DevAi2x_MvccPds4) {
        }
    }
})
