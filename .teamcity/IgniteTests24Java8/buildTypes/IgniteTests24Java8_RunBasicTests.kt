package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.VcsTrigger
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object IgniteTests24Java8_RunBasicTests : BuildType({
    name = "--> Run :: Basic Tests"
    description = "Runs fastest test suites to get reply as fast as possible. Use -Run All for check changes"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        showDependenciesChanges = true
    }

    triggers {
        vcs {
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_DEFAULT
            branchFilter = """
                +:<default>
                +:pull/3667/head
                +:ignite-2.5
            """.trimIndent()
        }
    }

    dependencies {
        snapshot(IgniteTests24Java8_Basic1) {
        }
        snapshot(IgniteTests24Java8_Basic3) {
        }
        snapshot(IgniteTests24Java8_CacheFullApi) {
        }
        snapshot(IgniteTests24Java8_ComputeGrid) {
        }
        snapshot(IgniteTests24Java8_InspectionsCore) {
        }
        snapshot(IgniteTests24Java8_Javadoc) {
        }
        snapshot(IgniteTests24Java8_LicensesHeaders) {
        }
        snapshot(IgniteTests24Java8_MissingTests) {
        }
        snapshot(IgniteTests24Java8_PdsUnitTests) {
        }
        snapshot(IgniteTests24Java8_PlatformNetCoreLinux) {
        }
        snapshot(IgniteTests24Java8_Security) {
        }
    }
})
