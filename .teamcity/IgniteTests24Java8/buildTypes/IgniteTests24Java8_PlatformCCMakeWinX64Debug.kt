package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object IgniteTests24Java8_PlatformCCMakeWinX64Debug : BuildType({
    templates(IgniteTests24Java8_C, IgniteTests24Java8_RunTestsJava, IgniteTests24Java8_RunNodeLibs)
    name = "Platform C++ CMake (Win x64 | Debug)"

    params {
        text("env.BOOST_LIBRARYDIR", """%env.BOOST_HOME%\lib64-msvc-10.0""", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("SCALA_PROFILE", "-scala", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.CPP_DIR", """%env.IGNITE_HOME%\modules\platforms\cpp""", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.BOOST_INCLUDEDIR", "%env.BOOST_HOME%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("BUILD_PROFILE", "Debug", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.ODBCDIR", """C:\odbc\amd64""", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.OPENSSL_ROOT_DIR", "%env.OPENSSL_HOME%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("BUILD_PROFILE_NAME", "debug", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("IGNITE_ODBC_PROFILE", "ignite-odbc-amd64-%BUILD_PROFILE_NAME%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.CPP_STAGING", """%env.CPP_DIR%\cpp_staging""", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("PATH__M2_REPOSITORY", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "Install built artifacts to local maven repository"
            id = "RUNNER_287"
            scriptContent = """
                @echo on
                
                
                FOR /f "tokens=* delims=" %%%%F in ('"%teamcity.tool.maven.DEFAULT%\bin\mvn.cmd help:evaluate -Dexpression=settings.localRepository -q -DforceStdout"') do @set "PATH__M2_REPOSITORY=%%%%F"
                del /s /f /q %PATH__M2_REPOSITORY%\org\apache\ignite
                xcopy repository\* %PATH__M2_REPOSITORY%\ /s
                exit /b
            """.trimIndent()
        }
        script {
            name = "Build C++"
            id = "RUNNER_36"
            workingDir = "%env.CPP_DIR%"
            scriptContent = """
                @echo on
                
                mkdir cmake-build-%BUILD_PROFILE_NAME%
                cd cmake-build-%BUILD_PROFILE_NAME%
                
                cmake -DWITH_ODBC=ON -DWITH_ODBC_MSI=ON -DWITH_THIN_CLIENT=ON -DWITH_TESTS=ON -DWARNINGS_AS_ERRORS=ON -DCMAKE_BUILD_TYPE=%BUILD_PROFILE% -DCMAKE_GENERATOR_PLATFORM=x64 -DCMAKE_INSTALL_PREFIX=%env.CPP_STAGING% ..
                cmake --build . --target install --config %BUILD_PROFILE% -v
            """.trimIndent()
        }
        script {
            name = "Build C++ Examples"
            id = "RUNNER_83"
            workingDir = """%env.CPP_DIR%\examples"""
            scriptContent = """
                @echo on
                
                mkdir cmake-build-%BUILD_PROFILE_NAME%
                cd cmake-build-%BUILD_PROFILE_NAME%
                
                cmake -DCMAKE_BUILD_TYPE=%BUILD_PROFILE% -DCMAKE_GENERATOR_PLATFORM=x64 -DIGNITE_CPP_DIR=%env.CPP_STAGING% ..
                cmake --build . --config %BUILD_PROFILE% -v
            """.trimIndent()
        }
        script {
            name = "Uninstall ODBC driver"
            id = "RUNNER_75"
            scriptContent = """
                @echo on
                chcp 65001
                wmic product where 'Vendor like "%%%%Apache%%%%"' call uninstall || exit 0
            """.trimIndent()
        }
        script {
            name = "Install ODBC driver"
            id = "RUNNER_61"
            workingDir = """%env.CPP_STAGING%\bin"""
            scriptContent = """
                @echo on
                
                msiexec.exe /i ignite-odbc-amd64.msi /qn
            """.trimIndent()
        }
        script {
            name = "Run Core tests"
            id = "RUNNER_151"
            workingDir = """%env.CPP_DIR%\cmake-build-%BUILD_PROFILE_NAME%"""
            scriptContent = """
                @echo on
                
                ctest -V -R IgniteCoreTest -C %BUILD_PROFILE% || echo "Some tests failed"
            """.trimIndent()
        }
        script {
            name = "Run ODBC tests"
            id = "RUNNER_152"
            workingDir = """%env.CPP_DIR%\cmake-build-%BUILD_PROFILE_NAME%"""
            scriptContent = """
                @echo on
                
                echo "Running ODBC tests"
                ctest -V -R IgniteOdbcTest -C %BUILD_PROFILE% || echo "Some tests failed"
            """.trimIndent()
        }
        script {
            name = "Run Thin Client tests"
            id = "RUNNER_194"
            workingDir = """%env.CPP_DIR%\cmake-build-%BUILD_PROFILE_NAME%"""
            scriptContent = """
                @echo on
                
                ctest -V -R IgniteThinClientTest -C %BUILD_PROFILE% || echo "Some tests failed"
            """.trimIndent()
        }
        powerShell {
            name = "Fix example configs"
            id = "RUNNER_58"
            workingDir = "%env.CPP_DIR%/examples"
            scriptMode = script {
                content = """
                    Get-ChildItem -Recurse -Filter '*.xml' | 
                    Foreach-Object {
                        ${'$'}content = Get-Content ${'$'}_.FullName
                    
                        ${'$'}regex = 'class="org.apache.ignite.configuration.IgniteConfiguration">'
                    	
                        ${'$'}content -replace ${'$'}regex, 'class="org.apache.ignite.configuration.IgniteConfiguration"><property name="localHost" value="127.0.0.1"/><property name="connectorConfiguration"><null/></property>' | Set-Content ${'$'}_.FullName
                    }
                """.trimIndent()
            }
            param("jetbrains_powershell_script_file", """Get-ChildItem -Recurse -Filter '*.xml' |  Foreach-Object {     ${'$'}content = Get-Content ${'$'}_.FullName      ${'$'}regex = 'class="org.apache.ignite.configuration.IgniteConfiguration">' 	     ${'$'}content -replace ${'$'}regex, 'class="org.apache.ignite.configuration.IgniteConfiguration"><property name="localHost" value="127.0.0.1"/><property name="connectorConfiguration"><null/></property>' | Set-Content ${'$'}_.FullName }""")
        }
        script {
            name = "Run Put-Get example"
            id = "RUNNER_38"
            workingDir = """%env.CPP_DIR%\examples\cmake-build-%BUILD_PROFILE_NAME%\%BUILD_PROFILE%"""
            scriptContent = """
                @echo on
                
                mkdir platforms\cpp\examples\put-get-example
                xcopy /s /e /f /y ..\..\put-get-example\config platforms\cpp\examples\put-get-example\config\
                
                SET PATH=%env.PATH%;%env.CPP_STAGING%\bin
                
                ignite-put-get-example.exe
            """.trimIndent()
        }
        script {
            name = "Run Query example"
            id = "RUNNER_40"
            workingDir = """%env.CPP_DIR%\examples\cmake-build-%BUILD_PROFILE_NAME%\%BUILD_PROFILE%"""
            scriptContent = """
                @echo on
                
                mkdir platforms\cpp\examples\query-example
                xcopy /s /e /f /y ..\..\query-example\config platforms\cpp\examples\query-example\config\
                
                SET PATH=%env.PATH%;%env.CPP_STAGING%\bin
                
                ignite-query-example.exe
            """.trimIndent()
        }
        script {
            name = "Run ODBC example"
            id = "RUNNER_41"
            workingDir = """%env.CPP_DIR%\examples\cmake-build-%BUILD_PROFILE_NAME%\%BUILD_PROFILE%"""
            scriptContent = """
                @echo on
                
                mkdir platforms\cpp\examples\odbc-example
                xcopy /s /e /f /y ..\..\odbc-example\config platforms\cpp\examples\odbc-example\config\
                
                SET PATH=%env.PATH%;%env.CPP_STAGING%\bin
                
                ignite-odbc-example.exe
            """.trimIndent()
        }
        script {
            name = "Run Continuous Query example"
            id = "RUNNER_42"
            workingDir = """%env.CPP_DIR%\examples\cmake-build-%BUILD_PROFILE_NAME%\%BUILD_PROFILE%"""
            scriptContent = """
                @echo on
                
                mkdir platforms\cpp\examples\continuous-query-example
                xcopy /s /e /f /y ..\..\continuous-query-example\config platforms\cpp\examples\continuous-query-example\config\
                
                SET PATH=%env.PATH%;%env.CPP_STAGING%\bin
                
                ignite-continuous-query-example.exe
            """.trimIndent()
        }
        script {
            name = "Run Compute example"
            id = "RUNNER_44"
            workingDir = """%env.CPP_DIR%\examples\cmake-build-%BUILD_PROFILE_NAME%\%BUILD_PROFILE%"""
            scriptContent = """
                @echo on
                
                mkdir platforms\cpp\examples\compute-example
                xcopy /s /e /f /y ..\..\compute-example\config platforms\cpp\examples\compute-example\config\
                
                SET PATH=%env.PATH%;%env.CPP_STAGING%\bin
                
                ignite-compute-example.exe
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_225", "RUNNER_265", "RUNNER_36", "RUNNER_83", "RUNNER_75", "RUNNER_61", "RUNNER_151", "RUNNER_152", "RUNNER_194", "RUNNER_58", "RUNNER_38", "RUNNER_40", "RUNNER_41", "RUNNER_42", "RUNNER_44", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 120
    }

    requirements {
        exists("DotNetFramework4.0_x86", "RQ_14")
        contains("teamcity.agent.jvm.os.name", "Windows", "RQ_15")
    }
    
    disableSettings("RQ_10", "RQ_14", "RUNNER_264", "RUNNER_265", "RUNNER_266")
})
