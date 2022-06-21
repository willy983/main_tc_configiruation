package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.NUnitStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.msBuild
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetInstaller
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nunit
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_RunTestSuitesNet : Template({
    name = "Run test suites (.NET)"

    params {
        text("TARGET_RELEASE", "Debug", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_CATEGORIES_EXCLUDE_LIST", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("env.JAVA_HOME", "%env.JDK_ORA_8%")
        text("TEST_CATEGORIES_INCLUDE_LIST", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("ASSEMBLY_FILES_INCLUDE_LIST", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "Copy nUnit binaries"
            id = "RUNNER_32"
            scriptContent = """
                @echo on
                
                mkdir modules\platforms\dotnet\libs\
                copy /y %teamcity.agent.home.dir%\plugins\dotnetPlugin\bin\Test\NUnit-2.6.3\v2.0\*.* modules\platforms\dotnet\libs\
            """.trimIndent()
        }
        nuGetInstaller {
            name = "NuGet restore"
            id = "RUNNER_33"
            toolPath = "%teamcity.tool.NuGet.CommandLine.3.4.3%"
            projects = """modules\platforms\dotnet\Apache.Ignite.sln"""
        }
        msBuild {
            name = "Build .Net"
            id = "RUNNER_43"
            workingDir = """modules\platforms\dotnet"""
            path = """modules\platforms\dotnet\Apache.Ignite.sln"""
            version = MSBuildStep.MSBuildVersion.V4_0
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V4_0
            platform = MSBuildStep.Platform.x64
            targets = "Clean;Rebuild"
            args = """
                /p:Configuration=%TARGET_RELEASE%
                /p:PlatformToolset=Windows7.1SDK
                /p:ShouldUnsetParentConfigurationAndPlatform=false
                /val /m /nologo /ds
            """.trimIndent()
        }
        script {
            name = "Copy test binaries"
            id = "RUNNER_54"
            scriptContent = """
                @echo on
                
                if exist modules\platforms\dotnet\Apache.Ignite.Core.Tests\bin\x64 (
                    xcopy modules\platforms\dotnet\Apache.Ignite.Core.Tests\bin\x64\*.* modules\platforms\dotnet\Apache.Ignite.Core.Tests\bin\ /s /e /y
                )
            """.trimIndent()
        }
        nunit {
            name = "Run .Net tests"
            id = "RUNNER_71"
            nunitVersion = NUnitStep.NUnitVersion.NUnit_2_6_4
            platform = NUnitStep.Platform.x64
            runtimeVersion = NUnitStep.RuntimeVersion.v4_0
            includeTests = "%ASSEMBLY_FILES_INCLUDE_LIST%"
            includeCategories = "%TEST_CATEGORIES_INCLUDE_LIST%"
            excludeCategories = "%TEST_CATEGORIES_EXCLUDE_LIST%"
            param("dotNetCoverage.dotCover.filters", "+:Apache.Ignite.Core")
        }
    }
})
