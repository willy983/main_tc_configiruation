package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object IgniteExtensions_Tests_Checkstyle : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "[Checkstyle]"

    steps {
        maven {
            name = "Run Checkstyle"
            id = "RUNNER_141"
            goals = "validate"
            runnerArgs = """
                -Pcheckstyle
                -DskipTests
                -Dmaven.javadoc.skip=true
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
    }
})
