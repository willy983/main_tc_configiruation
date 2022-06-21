package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnText

object IgniteTests24Java8_Javadoc : BuildType({
    name = "[Javadoc]"

    params {
        select("env.JAVA_HOME", "%env.JDK_ORA_8%", label = "JDK version", description = "Select JDK version for all tests",
                options = listOf("JDK 8" to "%env.JDK_ORA_8%", "JDK 9" to "%env.JDK_ORA_9%", "JDK 10" to "%env.JDK_ORA_10%", "JDK 11" to "%env.JDK_OPEN_11%"))
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    steps {
        script {
            workingDir = "[HACK] Force JDK8 (until JDK11 compilation is supported)"
            scriptContent = """
                #!/usr/bin/env bash
                
                
                echo "##teamcity[setParameter name='env.JAVA_HOME' value='%env.JDK_ORA_8%']"
            """.trimIndent()
        }
        maven {
            name = "Check JAVADOC"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            goals = "package"
            pomLocation = ""
            runnerArgs = """
                -Pall-java,all-scala,gpg,release,lgpl,examples
                -DskipTests
                ${IgniteTests24Java8_BuildApacheIgnite.depParamRefs["MAVEN_MODULES_STRING"]}
            """.trimIndent()
            mavenVersion = custom {
                path = "%teamcity.tool.maven.3.6.0%"
            }
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
    }

    failureConditions {
        failOnText {
            conditionType = BuildFailureOnText.ConditionType.REGEXP
            pattern = """^\[WARNING\] Javadoc Warnings.*${'$'}"""
            failureMessage = "Javadoc broken"
            reverse = false
        }
    }

    dependencies {
        snapshot(IgniteTests24Java8_BuildApacheIgnite) {
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
