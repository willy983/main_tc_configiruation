package ignite3_Test_SanityChecks.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object ignite3_Test_SanityChecks_Pmd : BuildType({
    templates(ignite3.buildTypes.ignite3_ApacheIgniteBuildDependencyLinux)
    name = "PMD"
    description = "Check possible bugs on code using PMD"

    artifactRules = """
        target/site/pmd.html
        target/pmd.xml
    """.trimIndent()

    steps {
        maven {
            name = "PMD Check"
            id = "RUNNER_106"
            goals = "validate pmd:check"
            pomLocation = ""
            runnerArgs = """
                -Ppmd
                -Dmaven.all-checks.skip
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_11%"
        }
    }

    failureConditions {
        executionTimeoutMin = 10
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_7")
    }
})
