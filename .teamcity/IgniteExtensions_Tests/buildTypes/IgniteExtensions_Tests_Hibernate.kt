package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object IgniteExtensions_Tests_Hibernate : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Hibernate"

    params {
        text("DIR_EXTENSION", "hibernate-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        maven {
            name = "Run Extension's tests"
            id = "RUNNER_141"
            goals = "test"
            pomLocation = ""
            runnerArgs = """
                -f modules/%DIR_EXTENSION% -am
                -Dmaven.test.failure.ignore=true
                -DfailIfNoTests=false
                -Dignite.version=%IGNITE_VERSION%
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        stepsOrder = arrayListOf("RUNNER_140", "RUNNER_143", "RUNNER_141")
    }
})
