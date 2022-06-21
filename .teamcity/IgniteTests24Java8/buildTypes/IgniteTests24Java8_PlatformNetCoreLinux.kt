package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetRun
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dotnetTest
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnMetricChange

object IgniteTests24Java8_PlatformNetCoreLinux : BuildType({
    templates(IgniteTests24Java8_PreBuild, IgniteTests24Java8_PostBuild)
    name = "Platform .NET (Core Linux)"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
        ./dev-tools/IGNITE-*-*.patch => patch
        modules/platforms/dotnet/Apache.Ignite.Core.Tests.DotNetCore/bin/Debug/netcoreapp2.0/*.log => logs.zip
    """.trimIndent()

    params {
        param("env.IGNITE_BASELINE_AUTO_ADJUST_ENABLED", "false")
        text("env.PATH", "%teamcity.tool.maven.DEFAULT%/bin:%env.PATH%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("env.JAVA_HOME", "%env.JDK_ORA_8%")
        param("env.COMPlus_EnableAlternateStackCheck", "1")
    }

    steps {
        script {
            name = "Clear NuGet caches"
            id = "RUNNER_207"
            enabled = false
            scriptContent = "dotnet nuget locals all --clear"
        }
        script {
            name = "Diag Script"
            id = "RUNNER_168"
            enabled = false
            scriptContent = """
                locale
                locale -a
            """.trimIndent()
        }
        script {
            name = "Build .NET"
            id = "RUNNER_79"
            workingDir = "modules/platforms/dotnet"
            scriptContent = "pwsh build.ps1 -skipJava -skipDotNet -skipNuGet"
        }
        dotnetRun {
            name = "Run .NET (verify startup)"
            id = "RUNNER_167"
            enabled = false
            projects = "modules/platforms/dotnet/*/Apache.Ignite.Core.Tests.DotNetCore.csproj"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        dotnetTest {
            name = "Test .NET"
            id = "RUNNER_80"
            projects = "modules/platforms/dotnet/*/Apache.Ignite.Core.Tests.DotNetCore.csproj"
            args = "-v d"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        script {
            name = "Build .NET (Mono)"
            id = "RUNNER_89"
            enabled = false
            workingDir = "modules/platforms/dotnet"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                #
                # Cleanup
                #
                for dir in bin obj; do
                    find ./ -name "${'$'}{dir}" -exec rm -rfv {} \;
                done
                
                #
                # Prepare build
                #
                curl https://api.nuget.org/downloads/nuget.exe -o nuget.exe
                mono nuget.exe restore Apache.Ignite.sln
                msbuild Apache.Ignite.sln /p:RunCodeAnalysis=false
            """.trimIndent()
        }
        script {
            name = "Test .NET (Mono)"
            id = "RUNNER_124"
            enabled = false
            workingDir = "modules/platforms/dotnet"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                mono Apache.Ignite.Core.Tests/bin/Debug/Apache.Ignite.Core.Tests.exe -basicTests
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_207", "RUNNER_168", "RUNNER_79", "RUNNER_167", "RUNNER_80", "RUNNER_89", "RUNNER_124", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 120
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

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_34")
    }
    
    disableSettings("swabra")
})
