package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_RunCache : BuildType({
    name = "-> Run :: Cache"
    description = "Dummy build for run all build in project by one click"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        showDependenciesChanges = true
    }

    dependencies {
        snapshot(DevAi2x_Cache1) {
        }
        snapshot(DevAi2x_Cache2) {
        }
        snapshot(DevAi2x_Cache3) {
        }
        snapshot(DevAi2x_Cache4) {
        }
        snapshot(DevAi2x_Cache5) {
        }
        snapshot(DevAi2x_Cache6) {
        }
        snapshot(DevAi2x_Cache7) {
        }
        snapshot(DevAi2x_Cache8) {
        }
        snapshot(DevAi2x_Cache9) {
        }
        snapshot(DevAi2x_CacheDeadlockDetection) {
        }
        snapshot(DevAi2x_CacheExpiryPolicy) {
        }
        snapshot(DevAi2x_CacheFailover1) {
        }
        snapshot(DevAi2x_CacheFailover2) {
        }
        snapshot(DevAi2x_CacheFailover3) {
        }
        snapshot(DevAi2x_CacheFailoverSsl) {
        }
        snapshot(DevAi2x_CacheFullApi) {
        }
        snapshot(DevAi2x_CacheFullApiConfigVariationsBasic) {
        }
        snapshot(DevAi2x_CacheFullApiConfigVariationsWithKeepBinary) {
        }
        snapshot(DevAi2x_CacheFullApiMultiJvm) {
        }
        snapshot(DevAi2x_CacheRestarts1) {
        }
        snapshot(DevAi2x_CacheRestarts2) {
        }
        snapshot(DevAi2x_CacheTxRecovery) {
        }
        snapshot(DevAi2x_DataStructures) {
        }
        snapshot(DevAi2x_JCacheTck11) {
        }
        snapshot(DevAi2x_Queries1) {
        }
    }
})
