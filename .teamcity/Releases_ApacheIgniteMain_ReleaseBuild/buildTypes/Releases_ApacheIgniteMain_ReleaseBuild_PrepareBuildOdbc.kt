package Releases_ApacheIgniteMain_ReleaseBuild.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.PowerShellStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.VisualStudioStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.visualStudio

object Releases_ApacheIgniteMain_ReleaseBuild_PrepareBuildOdbc : BuildType({
    name = "[1] Prepare & Build ODBC"

    artifactRules = """
        %IGNITE_ROOT%\modules\platforms\cpp\install\amd64\bin\*.msi => ignite.odbc.installers.zip
        %IGNITE_ROOT%\modules\platforms\cpp\install\x86\bin\*.msi => ignite.odbc.installers.zip
        vote.patch
    """.trimIndent()

    params {
        param("env.OPENSSL_HOME", """C:\OpenSSL-Win64""")
        param("system.JAVA_HOME", "%env.JAVA_HOME%")
        text("IGNITE_ROOT", "ignite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("IGNITE_VERSION", "")
        param("RC_NAME", "")
        param("env.JAVA_HOME", "%env.JDK_ORA_8%")
        param("env.OPENSSL_HOME_X86", """C:\OpenSSL-Win32""")
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite, "+:. => ./ignite")

        cleanCheckout = true
        excludeDefaultBranchChanges = true
    }

    steps {
        script {
            name = "Add NuGet executable to PATH"
            enabled = false
            scriptContent = """
                @echo on
                
                echo ##teamcity[setParameter name='env.PATH' value='%%PATH%%;%teamcity.tool.NuGet.CommandLine.DEFAULT%\tools']
            """.trimIndent()
        }
        script {
            name = "Git setup (Ignore chmod settings)"
            workingDir = "%IGNITE_ROOT%"
            scriptContent = "git config core.filemode false"
        }
        maven {
            name = "[OLD] Change maven version"
            enabled = false
            goals = "versions:set"
            pomLocation = """%IGNITE_ROOT%\pom.xml"""
            runnerArgs = """
                -DnewVersion=%IGNITE_VERSION%
                -Pall-java,all-scala,all-other
                -DgenerateBackupPoms=false
                -DgroupId=* -DartifactId=* -DoldVersion=*
                -DprocessDependencies=false
            """.trimIndent()
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
        }
        powerShell {
            name = "[NEW] Change MAVEN version"
            scriptMode = script {
                content = """
                    ${'$'}version = "%IGNITE_VERSION%"
                    ${'$'}filePath = "%teamcity.agent.work.dir%\parent\pom.xml"
                    
                    (GC ${'$'}filePath).Replace("<revision>.*<revision>", "<revision>${'$'}version<revision>") | Set-Content ${'$'}filePath
                """.trimIndent()
            }
        }
        maven {
            name = "Change dotnet & cpp versions"
            goals = "validate"
            pomLocation = """%IGNITE_ROOT%\pom.xml"""
            runnerArgs = """
                -P update-versions
                -Dnew.ignite.version=%IGNITE_VERSION%
            """.trimIndent()
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
        }
        script {
            name = "Git patch (with changed versions) generation"
            workingDir = "%IGNITE_ROOT%"
            scriptContent = "git diff > ../vote.patch"
        }
        powerShell {
            name = "Build Ignite.NET"
            enabled = false
            platform = PowerShellStep.Platform.x64
            edition = PowerShellStep.Edition.Desktop
            workingDir = "ignite/modules/platforms/dotnet"
            scriptMode = file {
                path = "ignite/modules/platforms/dotnet/build.ps1"
            }
            noProfile = false
            param("jetbrains_powershell_scriptArguments", "-skipJava -skipNuget")
        }
        visualStudio {
            name = "Build 32-bit ODBC binary"
            enabled = false
            path = "ignite/modules/platforms/cpp/project/vs/ignite.sln"
            version = VisualStudioStep.VisualStudioVersion.vs2010
            runPlatform = VisualStudioStep.Platform.x86
            msBuildVersion = VisualStudioStep.MSBuildVersion.V4_0
            msBuildToolsVersion = VisualStudioStep.MSBuildToolsVersion.V4_0
            targets = "odbc:Rebuild"
            configuration = "Release"
            platform = "Win32"
        }
        visualStudio {
            name = "Build 64-bit ODBC binary"
            enabled = false
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            path = "ignite/modules/platforms/cpp/project/vs/ignite.sln"
            version = VisualStudioStep.VisualStudioVersion.vs2010
            runPlatform = VisualStudioStep.Platform.x86
            msBuildVersion = VisualStudioStep.MSBuildVersion.V4_0
            msBuildToolsVersion = VisualStudioStep.MSBuildToolsVersion.V4_0
            targets = "odbc:Rebuild"
            configuration = "Release"
            platform = "x64"
        }
        script {
            name = "Build 32-bit ODBC installer"
            workingDir = """%IGNITE_ROOT%\modules\platforms\cpp"""
            scriptContent = """
                set OPENSSL_ROOT_DIR=%env.OPENSSL_HOME_X86%
                mkdir cmake-build-release-32
                cd cmake-build-release-32
                
                cmake -DWITH_CORE=OFF -DWITH_ODBC=ON -DWITH_ODBC_MSI=ON -DCMAKE_BUILD_TYPE=Release -DCMAKE_GENERATOR_PLATFORM=Win32 -DCMAKE_INSTALL_PREFIX=..\install\x86 ..
                cmake --build . --target install --config Release
            """.trimIndent()
        }
        script {
            name = "Build 64-bit ODBC installer"
            workingDir = """%IGNITE_ROOT%\modules\platforms\cpp"""
            scriptContent = """
                set OPENSSL_ROOT_DIR=%env.OPENSSL_HOME%
                mkdir cmake-build-release-64
                cd cmake-build-release-64
                
                cmake -DWITH_CORE=OFF -DWITH_ODBC=ON -DWITH_ODBC_MSI=ON -DCMAKE_BUILD_TYPE=Release -DCMAKE_GENERATOR_PLATFORM=x64 -DCMAKE_INSTALL_PREFIX=..\install\amd64 ..
                cmake --build . --target install --config Release
            """.trimIndent()
        }
        script {
            name = "Build dotnetdoc"
            enabled = false
            workingDir = "ignite/modules/platforms/dotnet/docfx"
            scriptContent = "generate-docs.cmd"
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Windows 10")
    }
})
