package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_RunCpp : BuildType({
    name = "-> Run :: CPP"
    description = "Run all C++ related suites"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        showDependenciesChanges = true
    }

    dependencies {
        snapshot(IgniteTests24Java8_CheckCodeStyleDucktests) {
        }
        snapshot(IgniteTests24Java8_Javadoc) {
        }
        snapshot(IgniteTests24Java8_LicensesHeaders) {
        }
        snapshot(IgniteTests24Java8_PlatformCCMakeWinX64Debug) {
        }
        snapshot(IgniteTests24Java8_PlatformCCMakeWinX64Release) {
        }
        snapshot(IgniteTests24Java8_PlatformCPPCMakeLinux) {
        }
        snapshot(IgniteTests24Java8_PlatformCPPCMakeLinuxClang) {
        }
    }
})
