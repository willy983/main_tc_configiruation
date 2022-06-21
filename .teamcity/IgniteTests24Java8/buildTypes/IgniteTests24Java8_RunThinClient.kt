package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_RunThinClient : BuildType({
    name = "-> Run :: Thin Client"
    description = "Run all Thin Client suites"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        showDependenciesChanges = true
    }

    dependencies {
        snapshot(IgniteTests24Java8_CheckCodeStyle) {
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
        snapshot(IgniteTests24Java8_RunAllNet) {
        }
        snapshot(IgniteTests24Java8_ThinClientJava) {
        }
        snapshot(IgniteTests24Java8_ThinClientNodeJs) {
        }
        snapshot(IgniteTests24Java8_ThinClientPhp) {
        }
        snapshot(IgniteTests24Java8_WiPThinClientPython) {
        }
    }
})
