package ignite3_Test_WindowsTests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object ignite3_Test_WindowsTests_UnitTests : BuildType({
    templates(ignite3.buildTypes.ignite3_ApacheIgniteBuildDependencyWindows)
    name = "Unit Tests"

    params {
        text("JVM_OPTS", "", label = "JVM_OPTS", description = "Additional JVM parameters", allowEmpty = true)
    }

    steps {
        maven {
            name = "Run tests"
            id = "RUNNER_25"
            goals = "surefire:test"
            pomLocation = ""
            runnerArgs = """
                -pl -:ignite-examples
                -Dmaven.test.failure.ignore=true
                -DfailIfNoTests=false
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_11%"
            jvmArgs = "%JVM_OPTS%"
        }
    }
})
