package Releases_IgniteH2.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object Releases_IgniteH2_ReleaseBuild : BuildType({
    name = "[1] Release Build"

    artifactRules = "h2/target/*.jar"

    params {
        text("env.JAVA_HOME", "%env.JDK_ORA_8%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubH2fork4igniteSqlEngine)

        cleanCheckout = true
    }

    steps {
        maven {
            goals = "package"
            pomLocation = ""
            runnerArgs = "-DskipTests"
            workingDir = "h2"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
        }
    }
})
