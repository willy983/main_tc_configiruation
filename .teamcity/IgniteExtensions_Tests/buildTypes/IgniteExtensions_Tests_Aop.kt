package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object IgniteExtensions_Tests_Aop : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "AOP"

    params {
        text("DIR_EXTENSION", "aop-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        maven {
            name = "Add JDK9+ libraries to local Maven repository (~/.m2/repository) [JDK8]"
            id = "RUNNER_195"
            goals = "org.apache.maven.plugins:maven-dependency-plugin:2.8:get"
            runnerArgs = "-Dartifact=javax.transaction:javax.transaction-api:1.3"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        maven {
            name = "Run Extension's tests"
            id = "RUNNER_141"
            goals = "surefire:test"
            pomLocation = ""
            runnerArgs = """
                -pl modules/%DIR_EXTENSION% -am
                -Dmaven.test.failure.ignore=true
                -DfailIfNoTests=false
                -Dmaven.javadoc.skip=true
                -Dignite.version=%IGNITE_VERSION%
                -Dtest=IgniteAopSelfTestSuite
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        stepsOrder = arrayListOf("RUNNER_140", "RUNNER_143", "RUNNER_195", "RUNNER_141")
    }
})
