package ignite3_Test_WindowsTests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object ignite3_Test_WindowsTests_IntegrationTests : BuildType({
    name = "Integration Tests"

    params {
        text("JVM_OPTS", "", label = "JVM_OPTS", description = "Additional JVM parameters", allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
        showDependenciesChanges = true
    }

    steps {
        maven {
            name = "Build + Run tests"
            goals = "integration-test"
            pomLocation = ""
            runnerArgs = """
                -Dskip.surefire.tests
                -Dmaven.test.failure.ignore=true
                -DfailIfNoTests=false
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_11%"
            jvmArgs = "%JVM_OPTS%"
        }
    }

    failureConditions {
        executionTimeoutMin = 240
    }

    requirements {
        startsWith("teamcity.agent.jvm.os.name", "Windows")
    }
})
