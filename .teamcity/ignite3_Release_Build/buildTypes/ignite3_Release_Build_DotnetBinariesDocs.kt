package ignite3_Release_Build.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.nuGetPackagesIndexer
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetPack
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetPublish

object ignite3_Release_Build_DotnetBinariesDocs : BuildType({
    name = "[1] .NET Binaries | Docs"

    artifactRules = """
        %DIR__DOTNET% => %DIR__DOTNET%
        %DIR__NUGET% => %DIR__NUGET%
    """.trimIndent()

    params {
        text("PROJECT_VERSION", "${ignite3_Release_Build_Configure.depParamRefs["PROJECT_VERSION"]}", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("PROJECT_NAME", "${ignite3_Release_Build_Configure.depParamRefs["PROJECT_NAME"]}", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("DIR__DOTNET", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("DIR__NUGET", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    steps {
        dotnetPublish {
            name = "Build binaries"
            projects = "Apache.Ignite"
            workingDir = "modules/platforms/dotnet"
            configuration = "Release"
            outputDir = "%teamcity.build.checkoutDir%/%DIR__DOTNET%"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetPack {
            name = "Build Nuget"
            projects = "Apache.Ignite"
            workingDir = "modules/platforms/dotnet"
            configuration = "Release"
            outputDir = "%teamcity.build.checkoutDir%/%DIR__NUGET%"
            args = "--include-source"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }

    features {
        nuGetPackagesIndexer {
            feed = "ignite3/ReleaseCandidate"
        }
    }

    dependencies {
        snapshot(ignite3_Release_Build_Configure) {
            onDependencyFailure = FailureAction.CANCEL
            onDependencyCancel = FailureAction.CANCEL
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
