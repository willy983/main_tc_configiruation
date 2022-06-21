package ignite3_Test_SanityChecks.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object ignite3_Test_SanityChecks_LicensesHeaders : BuildType({
    name = "Licenses Headers"
    description = "Check all files (except excluded ones) are compliant with Apache License 2.0"

    artifactRules = "target/rat.txt"

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    steps {
        maven {
            name = "Check License Headers by Rat Maven Plugin"
            goals = "apache-rat:check"
            pomLocation = ""
            runnerArgs = "-pl :apache-ignite"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_11%"
        }
    }

    failureConditions {
        executionTimeoutMin = 10
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
