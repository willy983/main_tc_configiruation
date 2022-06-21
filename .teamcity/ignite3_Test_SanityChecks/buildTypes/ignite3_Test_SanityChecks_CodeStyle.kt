package ignite3_Test_SanityChecks.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnText

object ignite3_Test_SanityChecks_CodeStyle : BuildType({
    name = "Code Style"
    description = "Check code's style rules with Checkstyle"

    artifactRules = """
        target/checkstyle.xml
        target/site/checkstyle-aggregate.html
    """.trimIndent()

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    steps {
        maven {
            name = "[OLD] Check Code Style by Maven plugin"
            enabled = false
            goals = "checkstyle:checkstyle-aggregate"
            pomLocation = ""
            userSettingsSelection = "local-proxy.xml"
            userSettingsPath = "settings.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_11%"
        }
        maven {
            name = "Check Code Style by Maven plugin"
            goals = "validate"
            pomLocation = ""
            runnerArgs = """
                -Pcheckstyle
                -Dmaven.all-checks.skip
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            userSettingsPath = "settings.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_11%"
        }
    }

    failureConditions {
        executionTimeoutMin = 10
        failOnText {
            conditionType = BuildFailureOnText.ConditionType.REGEXP
            pattern = "There.*[0-9]+ error(s)? reported by Checkstyle"
            failureMessage = "CheckStyle errors"
            reverse = false
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
