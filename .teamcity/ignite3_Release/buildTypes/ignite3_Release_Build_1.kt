package ignite3_Release.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule

object ignite3_Release_Build_1 : BuildType({
    id("ignite3_Release_Build")
    name = "[1] Build"

    artifactRules = """
        %DIR_BINARIES% => %DIR_BINARIES%
        %DIR_PACKAGES% => %DIR_PACKAGES%
        %DIR__NUGET% => %DIR__NUGET%
    """.trimIndent()

    params {
        text("reverse.dep.*.DIR_BINARIES", "binaries", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("reverse.dep.*.DIR__NUGET", "nuget", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("DIR_PACKAGES", "%reverse.dep.*.DIR_PACKAGES%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("DIR__DOTNET", "%reverse.dep.*.DIR__DOTNET%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("DIR__NUGET", "%reverse.dep.*.DIR__NUGET%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("DIR_BINARIES", "%reverse.dep.*.DIR_BINARIES%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("reverse.dep.*.DIR__DOTNET", "dotnet", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("reverse.dep.*.DIR_PACKAGES", "packages", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    triggers {
        schedule {
            schedulingPolicy = daily {
                hour = 0
            }
            branchFilter = "+:main"
            triggerBuild = always()
            withPendingChangesOnly = false
            enableQueueOptimization = false

            enforceCleanCheckout = true
            enforceCleanCheckoutForDependencies = true
        }
    }

    dependencies {
        dependency(ignite3_Release_Build.buildTypes.ignite3_Release_Build_DEB) {
            snapshot {
                onDependencyFailure = FailureAction.IGNORE
                onDependencyCancel = FailureAction.IGNORE
            }

            artifacts {
                cleanDestination = true
                artifactRules = "%DIR_PACKAGES% => %DIR_PACKAGES%"
            }
        }
        dependency(ignite3_Release_Build.buildTypes.ignite3_Release_Build_DotnetBinariesDocs) {
            snapshot {
            }

            artifacts {
                cleanDestination = true
                artifactRules = "%DIR__NUGET% => %DIR__NUGET%"
            }
        }
        dependency(ignite3_Release_Build.buildTypes.ignite3_Release_Build_JavaBinariesDocs) {
            snapshot {
                onDependencyFailure = FailureAction.CANCEL
                onDependencyCancel = FailureAction.CANCEL
            }

            artifacts {
                cleanDestination = true
                artifactRules = "%DIR_BINARIES% => %DIR_BINARIES%"
            }
        }
        dependency(ignite3_Release_Build.buildTypes.ignite3_Release_Build_RPM) {
            snapshot {
                onDependencyFailure = FailureAction.IGNORE
                onDependencyCancel = FailureAction.IGNORE
            }

            artifacts {
                cleanDestination = true
                artifactRules = "%DIR_PACKAGES% => %DIR_PACKAGES%"
            }
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
