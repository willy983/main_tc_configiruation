package ignite3_Test.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetTest
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object ignite3_Test_RunNetTests : BuildType({
    templates(ignite3.buildTypes.ignite3_ApacheIgniteBuildDependencyLinux)
    name = "> Run :: .NET Tests"

    params {
        text("PATH__WORKING_DIR", "modules/platforms/dotnet/", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        dotnetBuild {
            name = "Build .NET"
            id = "RUNNER_25"
            workingDir = "%PATH__WORKING_DIR%"
            sdk = "3.1"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetTest {
            name = "Run .NET tests"
            id = "RUNNER_183"
            workingDir = "%PATH__WORKING_DIR%"
            skipBuild = true
            sdk = "3.1"
            coverage = dotcover {
                toolPath = "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%"
            }
        }
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            enabled = false
        }
    }

    failureConditions {
        executionTimeoutMin = 15
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_18")
    }
})
