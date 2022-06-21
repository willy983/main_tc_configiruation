package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.Swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnMetricChange

object IgniteTests24Java8_PlatformNetWindows : BuildType({
    templates(IgniteTests24Java8_PreBuild, IgniteTests24Java8_PostBuild)
    name = "Platform .NET (Windows)"

    params {
        param("env.IGNITE_BASELINE_AUTO_ADJUST_ENABLED", "false")
        text("env.PATH", "%teamcity.tool.maven.DEFAULT%/bin:%env.PATH%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.JAVA_HOME", "%env.JDK_ORA_8%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("env.COMPlus_EnableAlternateStackCheck", "1")
    }

    steps {
        script {
            name = "Build .NET"
            id = "RUNNER_79"
            workingDir = "modules/platforms/dotnet"
            scriptContent = "dotnet build Apache.Ignite.sln"
        }
        powerShell {
            name = "NUnit: Apache.Ignite.Core.Tests"
            id = "RUNNER_197"
            workingDir = "modules/platforms/dotnet/Apache.Ignite.Core.Tests/bin/Debug/net461"
            scriptMode = script {
                content = """
                    ${'$'}runner = "%system.teamcity.build.checkoutDir%/modules/platforms/dotnet/Apache.Ignite.Core.Tests/bin/Debug/net461/nunit/nunit3-console.exe"
                    iex "& ${'$'}runner Apache.Ignite.Core.Tests.exe --teamcity"
                    
                    ${'$'}res = ${'$'}lastexitcode
                    
                    if (${'$'}res -lt 0) {
                      echo "Failed to run tests"
                      exit ${'$'}res
                    }
                """.trimIndent()
            }
        }
        powerShell {
            name = "NUnit: Apache.Ignite.AspNet.Tests"
            id = "RUNNER_198"
            workingDir = "modules/platforms/dotnet/Apache.Ignite.AspNet.Tests/bin/Debug/net461"
            scriptMode = script {
                content = """
                    ${'$'}runner = "%system.teamcity.build.checkoutDir%/modules/platforms/dotnet/Apache.Ignite.Core.Tests/bin/Debug/net461/nunit/nunit3-console.exe"
                    iex "& ${'$'}runner Apache.Ignite.AspNet.Tests.dll --teamcity"
                    
                    ${'$'}res = ${'$'}lastexitcode
                    
                    if (${'$'}res -lt 0) {
                      echo "Failed to run tests"
                      exit ${'$'}res
                    }
                """.trimIndent()
            }
        }
        powerShell {
            name = "NUnit: Apache.Ignite.EntityFramework.Tests"
            id = "RUNNER_199"
            workingDir = "modules/platforms/dotnet/Apache.Ignite.EntityFramework.Tests/bin/Debug/net461"
            scriptMode = script {
                content = """
                    ${'$'}runner = "%system.teamcity.build.checkoutDir%/modules/platforms/dotnet/Apache.Ignite.Core.Tests/bin/Debug/net461/nunit/nunit3-console.exe"
                    iex "& ${'$'}runner Apache.Ignite.EntityFramework.Tests.dll --teamcity"
                    
                    ${'$'}res = ${'$'}lastexitcode
                    
                    if (${'$'}res -lt 0) {
                      echo "Failed to run tests"
                      exit ${'$'}res
                    }
                """.trimIndent()
            }
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_79", "RUNNER_197", "RUNNER_198", "RUNNER_199", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 190
        failOnMetricChange {
            id = "BUILD_EXT_16"
            metric = BuildFailureOnMetric.MetricType.INSPECTION_ERROR_COUNT
            threshold = 0
            units = BuildFailureOnMetric.MetricUnit.DEFAULT_UNIT
            comparison = BuildFailureOnMetric.MetricComparison.MORE
            compareTo = value()
        }
        failOnMetricChange {
            id = "BUILD_EXT_17"
            metric = BuildFailureOnMetric.MetricType.INSPECTION_WARN_COUNT
            threshold = 0
            units = BuildFailureOnMetric.MetricUnit.DEFAULT_UNIT
            comparison = BuildFailureOnMetric.MetricComparison.MORE
            compareTo = value()
        }
    }

    features {
        swabra {
            id = "swabra"
            enabled = false
            forceCleanCheckout = true
            lockingProcesses = Swabra.LockingProcessPolicy.KILL
        }
    }

    requirements {
        exists("env.windir", "RQ_25")
    }
})
