package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.VisualStudioStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.visualStudio

object DevAi2x_PlatformCWinX64Debug : BuildType({
    templates(DevAi2x_RunTestSuitesJava, DevAi2x_C)
    name = "Platform C++ (Win x64 | Debug)"

    params {
        param("SCALA_PROFILE", "-scala")
        param("env.CPP_DIR", """%env.IGNITE_HOME%\modules\platforms\cpp""")
        param("env.BOOST_LIB32_PATH", """%env.BOOST_HOME%\lib32-msvc-10.0""")
        param("env.BOOST_LIB_SUFIX_D32", "vc100-mt-gd-1_58")
        param("env.ODBCDIR", """C:\odbc\amd64""")
        param("env.BOOST_LIB_SUFIX_R64", "vc100-mt-1_58")
        param("env.BOOST_LIB_SUFIX_D64", "vc100-mt-gd-1_58")
        param("IGNITE_ODBC_PROFILE", "ignite-odbc-amd64-debug")
        param("env.BOOST_LIB64_PATH", """%env.BOOST_HOME%\lib64-msvc-10.0""")
        param("env.BOOST_LIB_SUFIX_R32", "vc100-mt-1_58")
        text("env.JAVA_HOME", "%env.JDK_ORA_8%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        visualStudio {
            name = "Build C++"
            id = "RUNNER_83"
            path = "modules/platforms/cpp/project/vs/ignite.sln"
            version = VisualStudioStep.VisualStudioVersion.vs2010
            runPlatform = VisualStudioStep.Platform.x86
            msBuildVersion = VisualStudioStep.MSBuildVersion.V4_0
            msBuildToolsVersion = VisualStudioStep.MSBuildToolsVersion.V4_0
            targets = "Clean;Rebuild"
            configuration = "Debug"
            platform = "x64"
            args = "/p:PlatformToolset=Windows7.1SDK /p:ShouldUnsetParentConfigurationAndPlatform=false"
        }
        script {
            name = "Uninstall ODBC driver"
            id = "RUNNER_72"
            scriptContent = """
                @echo on
                chcp 65001
                wmic product where 'Vendor like "%%%%Apache%%%%"' call uninstall || exit 0
            """.trimIndent()
        }
        powerShell {
            name = "Create MSI-building script for ODBC driver"
            id = "RUNNER_66"
            workingDir = """%env.CPP_DIR%\odbc\install"""
            scriptMode = script {
                content = """cat ignite-odbc-amd64.wxs | %{${'$'}_ -replace "Release","Debug"} | out-file "%IGNITE_ODBC_PROFILE%.wxs" -encoding utf8"""
            }
        }
        script {
            name = "Create MSI installer for ODBC driver"
            id = "RUNNER_67"
            workingDir = """%env.CPP_DIR%\odbc\install"""
            scriptContent = """
                @echo on
                
                candle.exe %IGNITE_ODBC_PROFILE%.wxs || exit 1
                light.exe -ext WixUIExtension %IGNITE_ODBC_PROFILE%.wixobj || exit 1
            """.trimIndent()
        }
        script {
            name = "Install ODBC driver"
            id = "RUNNER_68"
            workingDir = """%env.CPP_DIR%\odbc\install"""
            scriptContent = """
                @echo on
                
                msiexec.exe /i %IGNITE_ODBC_PROFILE%.msi /qn
            """.trimIndent()
        }
        script {
            name = "Run Core tests"
            id = "RUNNER_151"
            workingDir = """modules\platforms\cpp\project\vs\x64\Debug"""
            scriptContent = """
                @echo on
                
                core-test.exe || echo "Some tests failed"
            """.trimIndent()
        }
        script {
            name = "Run ODBC tests"
            id = "RUNNER_152"
            workingDir = """modules\platforms\cpp\project\vs\x64\Debug"""
            scriptContent = """
                @echo on
                
                echo "Running ODBC tests"
                odbc-test.exe || echo "Some tests failed"
            """.trimIndent()
        }
        script {
            name = "Run Thin Client tests"
            id = "RUNNER_194"
            workingDir = """modules\platforms\cpp\project\vs\x64\Debug"""
            scriptContent = """
                @echo on
                
                IF EXIST thin-client-test.exe (
                    thin-client-test.exe || echo "Some tests failed"
                )
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_225", "RUNNER_265", "RUNNER_83", "RUNNER_72", "RUNNER_66", "RUNNER_67", "RUNNER_68", "RUNNER_151", "RUNNER_152", "RUNNER_194", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 60
    }

    requirements {
        exists("DotNetFramework4.0_x86", "RQ_14")
        contains("teamcity.agent.jvm.os.name", "Windows", "RQ_15")
    }
    
    disableSettings("RQ_10", "RQ_14", "RUNNER_265")
})
