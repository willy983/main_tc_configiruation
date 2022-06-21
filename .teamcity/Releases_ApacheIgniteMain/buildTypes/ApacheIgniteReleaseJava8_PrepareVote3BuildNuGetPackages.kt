package Releases_ApacheIgniteMain.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.nuGetPublish
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell

object ApacheIgniteReleaseJava8_PrepareVote3BuildNuGetPackages : BuildType({
    name = "[3] Build & Upload Nuget Staging Packages"
    description = "Pack NuGet from existing binaries"

    artifactRules = "**/*.nupkg"

    params {
        param("VOTE_BUILD_NUM", "")
    }

    steps {
        powerShell {
            name = "Unzip artifacts to src and bin folders"
            scriptMode = script {
                content = """
                    Add-Type -AssemblyName System.IO.Compression.FileSystem
                    function Unzip
                    {
                        param([string]${'$'}zipfile, [string]${'$'}outpath)
                    
                        [System.IO.Compression.ZipFile]::ExtractToDirectory(${'$'}zipfile, ${'$'}outpath)
                    }
                    
                    ${'$'}src = Get-ChildItem | Where-Object { ${'$'}_.Name -match "apache-ignite-[0-9].*?-src.zip" }
                    ${'$'}bin = Get-ChildItem | Where-Object { ${'$'}_.Name -match "apache-ignite-[0-9].*?-bin.zip" }
                    
                    echo ${'$'}src
                    echo ${'$'}bin
                    
                    Unzip ${'$'}src .
                    Unzip ${'$'}bin .
                    
                    rni (gi apache-ignite-*-src) src
                    rni (gi apache-ignite-*-bin) bin
                """.trimIndent()
            }
        }
        powerShell {
            name = "Pack NuGet"
            workingDir = """src\modules\platforms\dotnet\"""
            scriptMode = file {
                path = """src\modules\platforms\dotnet\build.ps1"""
            }
            param("jetbrains_powershell_scriptArguments", """-skipJava -skipDotNet -skipDotNetCore -jarDirs ..\bin\libs -asmDirs ..\..\..\..\bin\platforms\dotnet\bin""")
        }
        nuGetPublish {
            name = "NuGet staging publish"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            packages = "**/*.nupkg"
            serverUrl = "https://www.myget.org/F/apache-ignite-staging/"
            apiKey = "credentialsJSON:d8273238-09ca-48cf-a970-8f65e681d8dc"
        }
        powerShell {
            name = "Run NuGet verification script"
            workingDir = "src/modules/platforms/dotnet"
            scriptMode = script {
                content = """
                    if (Test-Path release/verify-nuget.ps1) {
                      ./release/verify-nuget.ps1
                    }
                """.trimIndent()
            }
        }
    }

    dependencies {
        artifacts(Releases_ApacheIgniteMain_ReleaseBuild_1) {
            buildRule = build("%VOTE_BUILD_NUM%")
            cleanDestination = true
            artifactRules = """
                release*.zip!svn/vote/apache-ignite-*-src.zip
                release*.zip!svn/vote/apache-ignite-*-bin.zip
            """.trimIndent()
        }
    }

    requirements {
        contains("env.OS", "Windows")
    }
})
