package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object IgniteExtensions_Tests_Kafka : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Kafka"

    params {
        text("DIR_EXTENSION", "kafka-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        maven {
            name = "Run Extension's tests"
            id = "RUNNER_141"
            goals = "test"
            pomLocation = ""
            runnerArgs = """
                -pl modules/%DIR_EXTENSION% -am -amd
                -Dmaven.test.failure.ignore=true
                -DfailIfNoTests=false
                -Dignite.version=%IGNITE_VERSION%
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
    }
})
