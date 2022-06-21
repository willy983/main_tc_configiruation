package Releases_ApacheIgniteNightly.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object Releases_NightlyRelease_ApacheIgniteNightlyReleaseBuildNetCpp : BuildType({
    name = "[APACHE IGNITE NIGHTLY RELEASE] #1 :: Build .Net & C++"

    artifactRules = """
        modules\platforms\cpp\install\amd64\bin\*.msi => ignite.odbc.installers.zip
        modules\platforms\cpp\install\x86\bin\*.msi => ignite.odbc.installers.zip
    """.trimIndent()

    params {
        text("env.OPENSSL_HOME", """C:\OpenSSL-Win64""", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("IGNITE_VERSION", "${Releases_NightlyRelease_ApacheIgniteNightlyReleasePrepare.depParamRefs["IGNITE_VERSION"]}")
        text("env.JAVA_HOME", "%env.JDK_ORA_8%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("env.OPENSSL_HOME_X86", """C:\OpenSSL-Win32""")
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        cleanCheckout = true
    }

    steps {
        script {
            name = "Build 32-bit ODBC installer"
            workingDir = """modules\platforms\cpp"""
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
            workingDir = """modules\platforms\cpp"""
            scriptContent = """
                set OPENSSL_ROOT_DIR=%env.OPENSSL_HOME%
                mkdir cmake-build-release-64
                cd cmake-build-release-64
                
                cmake -DWITH_CORE=OFF -DWITH_ODBC=ON -DWITH_ODBC_MSI=ON -DCMAKE_BUILD_TYPE=Release -DCMAKE_GENERATOR_PLATFORM=x64 -DCMAKE_INSTALL_PREFIX=..\install\amd64 ..
                cmake --build . --target install --config Release
            """.trimIndent()
        }
    }

    dependencies {
        dependency(Releases_NightlyRelease_ApacheIgniteNightlyReleasePrepare) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                cleanDestination = true
                artifactRules = "apache-ignite-%IGNITE_VERSION%-src.zip!** => ."
            }
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Windows 10")
    }
})
