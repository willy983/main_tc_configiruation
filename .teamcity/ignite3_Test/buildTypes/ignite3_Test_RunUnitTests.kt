package ignite3_Test.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object ignite3_Test_RunUnitTests : BuildType({
    templates(ignite3.buildTypes.ignite3_ApacheIgniteBuildDependencyLinux)
    name = "> Run :: Unit Tests"

    artifactRules = "**/*.dumpstream"

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
            param("teamcity.coverage.idea.includePatterns", "org.apache.ignite.*")
            param("teamcity.coverage.jacoco.classpath", "+:**/target/classes/org/apache/ignite/**/*.class")
            param("teamcity.tool.jacoco", "%teamcity.tool.jacoco.0.8.7%")
            param("teamcity.coverage.jacoco.patterns", "-:org.apache.ignite.internal.pagememory.freelist.AbstractFreeListTest")
        }
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            enabled = false
        }
    }

    failureConditions {
        executionTimeoutMin = 10
        failOnText {
            id = "BUILD_EXT_22"
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "LEAK:"
            failureMessage = "Netty buffer leak detected."
            reverse = false
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_18")
    }
})
