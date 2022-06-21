package ignite3_Test_SanityChecks.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object ignite3_Test_SanityChecks_LegacyApi : BuildType({
    templates(ignite3.buildTypes.ignite3_ApacheIgniteBuildDependencyLinux)
    name = "Legacy API"
    description = "Check code for Legacy API with Modernizer"

    steps {
        maven {
            name = "Check for Legacy API by Modernizer Maven plugin"
            id = "RUNNER_148"
            goals = "modernizer:modernizer"
            pomLocation = ""
            userSettingsSelection = "local-proxy.xml"
            userSettingsPath = "settings.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_11%"
        }
    }

    failureConditions {
        executionTimeoutMin = 10
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_43")
    }
})
