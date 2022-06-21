package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object IgniteTests24Java8_MissingTests : BuildType({
    name = "[Missing Tests]"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
        ./dev-tools/IGNITE-*-*.patch => patch
    """.trimIndent()

    params {
        text("env.JAVA_HOME", "%env.JDK_ORA_8%", allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        cleanCheckout = true
    }

    steps {
        maven {
            name = "Check for tests outside Test Suites"
            goals = "test"
            pomLocation = ""
            runnerArgs = "-Pcheck-test-suites,all-java,all-scala,scala"
            userSettingsSelection = "local-proxy.xml"
            userSettingsPath = "settings.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
    }

    failureConditions {
        executionTimeoutMin = 40
    }
})
