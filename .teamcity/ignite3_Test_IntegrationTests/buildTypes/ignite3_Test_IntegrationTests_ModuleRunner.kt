package ignite3_Test_IntegrationTests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object ignite3_Test_IntegrationTests_ModuleRunner : BuildType({
    templates(ignite3.buildTypes.ignite3_ApacheIgniteBuildDependencyLinux, ignite3_Test.buildTypes.ignite3_Test_IntegrationTests_1)
    name = "[Module] Runner"

    params {
        text("MODULE", ":ignite-runner", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("JVM_ARGS", """
            -Xmx2g
            -XX:MaxDirectMemorySize=256m
        """.trimIndent())
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            enabled = false
        }
    }

    failureConditions {
        executionTimeoutMin = 60
    }
})
