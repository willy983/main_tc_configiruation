package ignite3_Test_IntegrationTests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object ignite3_Test_IntegrationTests_ModuleClientHandler : BuildType({
    templates(ignite3.buildTypes.ignite3_ApacheIgniteBuildDependencyLinux, ignite3_Test.buildTypes.ignite3_Test_IntegrationTests_1)
    name = "[Module] Client Handler"

    params {
        text("MODULE", ":ignite-client-handler", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            enabled = false
        }
    }

    failureConditions {
        failOnText {
            id = "BUILD_EXT_24"
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "LEAK:"
            failureMessage = "Netty buffer leak detected."
            reverse = false
        }
    }
})
