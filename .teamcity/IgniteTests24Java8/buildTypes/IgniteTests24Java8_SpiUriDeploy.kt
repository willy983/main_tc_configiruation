package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object IgniteTests24Java8_SpiUriDeploy : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "SPI (URI Deploy)"

    params {
        text("MAVEN_MODULES", ":ignite-urideploy", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteUriDeploymentTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        maven {
            name = "Pre-compile external data"
            id = "RUNNER_105"
            goals = "compile"
            runnerArgs = """
                -pl :ignite-extdata-p2p,:ignite-extdata-uri -am
                -Dmaven.test.skip=true
                -Dmaven.javadoc.skip=true
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_225", "RUNNER_105", "RUNNER_265", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 20
    }
})
