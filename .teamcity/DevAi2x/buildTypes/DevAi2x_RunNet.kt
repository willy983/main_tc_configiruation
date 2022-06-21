package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_RunNet : BuildType({
    name = "-> Run :: .NET"
    description = "Run all .NET related suites"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        showDependenciesChanges = true
    }

    dependencies {
        snapshot(DevAi2x_PlatformNet) {
        }
        snapshot(DevAi2x_PlatformNetCore30Linux) {
        }
        snapshot(DevAi2x_PlatformNetCoreLinux) {
        }
        snapshot(DevAi2x_PlatformNetInspections) {
        }
        snapshot(DevAi2x_PlatformNetIntegrations) {
        }
        snapshot(DevAi2x_PlatformNetLongRunning) {
        }
        snapshot(DevAi2x_PlatformNetNuGet) {
        }
    }
})
