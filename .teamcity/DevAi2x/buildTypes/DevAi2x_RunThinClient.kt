package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_RunThinClient : BuildType({
    name = "-> Run :: Thin Client"
    description = "Run all Thin Client suites"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        showDependenciesChanges = true
    }

    dependencies {
        snapshot(DevAi2x_PlatformCCMakeLinux) {
        }
        snapshot(DevAi2x_PlatformCCMakeLinuxClang) {
        }
        snapshot(DevAi2x_PlatformCCMakeWinX64Debug) {
        }
        snapshot(DevAi2x_PlatformCCMakeWinX64Release) {
        }
        snapshot(DevAi2x_PlatformCWinX64Debug) {
        }
        snapshot(DevAi2x_PlatformCWinX64Release) {
        }
        snapshot(DevAi2x_ThinClientJava) {
        }
        snapshot(DevAi2x_ThinClientNodeJs) {
        }
        snapshot(DevAi2x_ThinClientPhp) {
        }
        snapshot(DevAi2x_ThinClientPython) {
        }
    }
})
