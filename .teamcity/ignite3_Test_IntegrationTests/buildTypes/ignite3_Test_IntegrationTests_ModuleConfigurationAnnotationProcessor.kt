package ignite3_Test_IntegrationTests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object ignite3_Test_IntegrationTests_ModuleConfigurationAnnotationProcessor : BuildType({
    templates(ignite3.buildTypes.ignite3_ApacheIgniteBuildDependencyLinux, ignite3_Test.buildTypes.ignite3_Test_IntegrationTests_1)
    name = "[Module] Configuration Annotation Processor"

    params {
        text("MODULE", ":ignite-configuration-annotation-processor", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            enabled = false
        }
    }
})
