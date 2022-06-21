package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MSBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.NUnitStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.PowerShellStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.msBuild
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nunit
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell

object DevAi2x_PlatformNetNuGet : BuildType({
    templates(DevAi2x_PreBuild, DevAi2x_RunTestSuitesNet, DevAi2x_PostBuild)
    name = "Platform .NET (NuGet)*"
    description = "Disabled dependency"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
        ./dev-tools/IGNITE-*-*.patch => patch
        modules/platforms/dotnet/nupkg => nupkg
    """.trimIndent()

    params {
        text("env.JAVA_HOME", "%env.JDK_ORA_18%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("TARGET_RELEASE", "Release")
        param("env.PATH", """%env.PATH%;%teamcity.tool.maven.DEFAULT%\bin""")
    }

    steps {
        maven {
            name = "Build Java"
            id = "RUNNER_96"
            enabled = false
            goals = "package"
            pomLocation = ""
            runnerArgs = """
                -U
                -P-lgpl,-scala,-examples,-test,-benchmarks
                -Dmaven.javadoc.skip=true
                -DskipTests
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        powerShell {
            name = "Build .NET"
            id = "RUNNER_122"
            platform = PowerShellStep.Platform.x64
            edition = PowerShellStep.Edition.Desktop
            workingDir = "modules/platforms/dotnet"
            scriptMode = file {
                path = "modules/platforms/dotnet/build.ps1"
            }
            param("jetbrains_powershell_scriptArguments", "-skipJava -skipDotNetCore")
        }
        powerShell {
            name = "NuGet pack, install, restore"
            id = "RUNNER_177"
            platform = PowerShellStep.Platform.x64
            edition = PowerShellStep.Edition.Desktop
            workingDir = """modules\platforms\dotnet\Apache.Ignite.Core.Tests.NuGet"""
            scriptMode = file {
                path = """modules\platforms\dotnet\Apache.Ignite.Core.Tests.NuGet\install-package.ps1"""
            }
            noProfile = false
            param("jetbrains_powershell_script_code", """
                ${'$'}ng = (Get-Item .).FullName + '\nuget.exe'
                
                if (!(Test-Path ${'$'}ng)) {
                    ${'$'}ng = 'nuget'
                }
                
                ${'$'}cfg = 'Release'
                ${'$'}ver = (gi ..\Apache.Ignite.Core\bin\${'$'}cfg\Apache.Ignite.Core.dll).VersionInfo.ProductVersion
                
                rmdir nupkg -Force -Recurse
                rmdir pkg -Force -Recurse
                
                mkdir nupkg
                mkdir pkg
                
                # Find all nuspec files and run 'nuget pack' either directly, or on corresponding csproj files (if present).
                ls ..\*.nuspec -Recurse  `
                    | % { If (Test-Path ([io.path]::ChangeExtension(${'$'}_.FullName, ".csproj"))){[io.path]::ChangeExtension(${'$'}_.FullName, ".csproj")} Else {${'$'}_.FullName}  } `
                    | % { & ${'$'}ng pack ${'$'}_ -Prop Configuration=${'$'}cfg -Version ${'$'}ver -Prop Platform=AnyCPU -OutputDirectory nupkg }
                
                # Replace versions in project files
                (Get-Content packages.config) `
                    -replace 'id="Apache.Ignite(.*?)" version=".*?"', ('id="Apache.Ignite${'$'}1" version="' + ${'$'}ver + '"') `
                    | Out-File packages.config -Encoding utf8
                
                (Get-Content Apache.Ignite.Core.Tests.NuGet.csproj) `
                    -replace 'packages\\Apache.Ignite(.*?)\.\d.*?\\', ('packages\Apache.Ignite${'$'}1.' + "${'$'}ver\") `
                    | Out-File Apache.Ignite.Core.Tests.NuGet.csproj  -Encoding utf8
                
                # restore packages
                & ${'$'}ng restore
                
                # refresh content files
                ls packages\*\content | % {copy (${'$'}_.FullName + "\*.*") .\ }
            """.trimIndent())
        }
        msBuild {
            name = "Build .NET NuGet tests"
            id = "RUNNER_163"
            workingDir = """modules\platforms\dotnet\Apache.Ignite.Core.Tests.NuGet"""
            path = """modules\platforms\dotnet\Apache.Ignite.Core.Tests.NuGet\Apache.Ignite.Core.Tests.NuGet.sln"""
            version = MSBuildStep.MSBuildVersion.V4_0
            toolsVersion = MSBuildStep.MSBuildToolsVersion.V4_0
            platform = MSBuildStep.Platform.x64
            targets = "Clean;Rebuild"
            args = """
                /p:Configuration=Release
                /p:Platform="Any CPU" /p:PlatformToolset=Windows7.1SDK /p:ShouldUnsetParentConfigurationAndPlatform=false
                /val /m /nologo /ds
            """.trimIndent()
        }
        nunit {
            name = "Run NuGet tests (x86)"
            id = "RUNNER_164"
            nunitVersion = NUnitStep.NUnitVersion.NUnit_2_6_3
            platform = NUnitStep.Platform.x86
            runtimeVersion = NUnitStep.RuntimeVersion.v4_0
            includeTests = """modules\platforms\dotnet\Apache.Ignite.Core.Tests.NuGet\bin\Release\Apache.Ignite.Core.Tests.NuGet.exe"""
        }
        nunit {
            name = "Run NuGet tests (x64)"
            id = "RUNNER_165"
            nunitVersion = NUnitStep.NUnitVersion.NUnit_2_6_3
            platform = NUnitStep.Platform.x64
            runtimeVersion = NUnitStep.RuntimeVersion.v4_0
            includeTests = """modules\platforms\dotnet\Apache.Ignite.Core.Tests.NuGet\bin\Release\Apache.Ignite.Core.Tests.NuGet.exe"""
        }
        powerShell {
            name = "Run NuGet verification script"
            id = "RUNNER_10"
            workingDir = "modules/platforms/dotnet"
            scriptMode = script {
                content = """
                    if (Test-Path release/verify-nuget.ps1) {
                      ./release/verify-nuget.ps1
                    }
                """.trimIndent()
            }
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_32", "RUNNER_33", "RUNNER_43", "RUNNER_54", "RUNNER_71", "RUNNER_96", "RUNNER_122", "RUNNER_177", "RUNNER_163", "RUNNER_164", "RUNNER_165", "RUNNER_10", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 120
    }
    
    disableSettings("RUNNER_32", "RUNNER_33", "RUNNER_43", "RUNNER_54", "RUNNER_71")
})
