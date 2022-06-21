package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnText

object DevAi2x_C : Template({
    name = "C++"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        ./dev-tools/IGNITE-*-*.patch => patch
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
    """.trimIndent()

    params {
        param("env.IGNITE_NATIVE_TEST_CLASSPATH", "true")
        param("env.IGNITE_NATIVE_TEST_CPP_THIN_CONFIG_PATH", "%env.IGNITE_HOME%/modules/platforms/cpp/thin-client-test/config")
        param("env.LD_LIBRARY_PATH", "%env.JAVA_HOME%/jre/lib/amd64/server:%env.JAVA_HOME%/lib/server:%env.CPP_STAGING%/lib")
        param("env.OPENSSL_HOME", """C:\OpenSSL-Win64""")
        param("env.IGNITE_HOME", "%teamcity.build.checkoutDir%")
        param("env.BOOST_TEST_CATCH_SYSTEM_ERRORS", "no")
        param("env.IGNITE_CPP_PRINT_STACK", "true")
        param("env.BOOST_HOME", """C:\local\boost_1_58_0""")
        param("env.ODBCDIR", "")
        param("env.IGNITE_NATIVE_TEST_CPP_CONFIG_PATH", "%env.IGNITE_HOME%/modules/platforms/cpp/core-test/config")
        param("env.IGNITE_NATIVE_TEST_ODBC_CONFIG_PATH", "%env.IGNITE_HOME%/modules/platforms/cpp/odbc-test/config")
        param("env.CPP_STAGING", "/tmp/cpp_staging")
        param("env.OPENSSL_HOME_X86", """C:\OpenSSL-Win32""")
    }

    vcs {
        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    failureConditions {
        executionTimeoutMin = 40
        failOnText {
            id = "BUILD_EXT_21"
            conditionType = BuildFailureOnText.ConditionType.REGEXP
            pattern = """"Process exited with error"${'$'}"""
            failureMessage = "Process exited with error"
            reverse = false
        }
        failOnText {
            id = "BUILD_EXT_35"
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "memory access violation occurred at address"
            failureMessage = "Memory access violation!"
            reverse = false
        }
        failOnText {
            id = "BUILD_EXT_36"
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "Detected memory leaks!"
            failureMessage = "Detected memory leaks!"
            reverse = false
        }
        failOnText {
            id = "BUILD_EXT_11"
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "SEGFAULT"
            failureMessage = "SEGFAULT detected"
            reverse = false
        }
    }
})
