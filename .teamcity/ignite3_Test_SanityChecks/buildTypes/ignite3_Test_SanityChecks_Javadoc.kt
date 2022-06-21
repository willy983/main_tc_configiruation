package ignite3_Test_SanityChecks.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnText

object ignite3_Test_SanityChecks_Javadoc : BuildType({
    templates(ignite3.buildTypes.ignite3_ApacheIgniteBuildDependencyLinux)
    name = "Javadoc"

    artifactRules = """
        target/site/apidocs => javadoc.zip
        target/checkstyle.xml
        target/site/checkstyle-aggregate.html
    """.trimIndent()

    steps {
        maven {
            name = "Check Javadoc style"
            id = "RUNNER_78"
            goals = "checkstyle:checkstyle-aggregate"
            pomLocation = ""
            runnerArgs = "-P javadoc-public-api"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_11%"
        }
        maven {
            name = "Check Javadoc"
            id = "RUNNER_180"
            goals = "javadoc:javadoc"
            pomLocation = ""
            runnerArgs = "-P javadoc"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_11%"
        }
        maven {
            name = "Build Javadoc"
            id = "RUNNER_182"
            goals = "javadoc:aggregate"
            pomLocation = ""
            runnerArgs = "-P javadoc"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_11%"
        }
        script {
            name = "Check internal packages"
            id = "RUNNER_181"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                #set -x
                
                
                PACKAGES="${'$'}(cat target/site/apidocs/index.html | grep org.apache.ignite | sed -r 's|.*html">(.*)</a.*|\1|' | grep internal || true)"
                if [ "${'$'}{PACKAGES}" != "" ]; then
                	echo "[ERROR] Internal packages detected"
                    for package in ${'$'}{PACKAGES}; do
                    	echo "    ${'$'}{package}"
                    done
                fi
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_175", "RUNNER_78", "RUNNER_180", "RUNNER_182", "RUNNER_181")
    }

    failureConditions {
        executionTimeoutMin = 10
        failOnText {
            id = "BUILD_EXT_13"
            enabled = false
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "[WARNING] Javadoc Warnings"
            failureMessage = "[ERROR] Javadoc Warnings"
            reverse = false
        }
        failOnText {
            id = "BUILD_EXT_14"
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "[ERROR] Internal packages detected"
            failureMessage = "[ERROR] Internal packages detected"
            reverse = false
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_16")
    }
})
