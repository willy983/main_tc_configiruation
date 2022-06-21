package IgniteThinClients_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object IgniteThinClients_Tests_Build : BuildType({
    name = "<Build>"
    description = "Build and prepare project for testing"

    artifactRules = """
        **/* => ignite.zip
        ./**/target/checkstyle-result.xml => checkstyle-result.zip
    """.trimIndent()

    params {
        param("env.JAVA_HOME", "%env.JDK_ORA_8%")
        text("OVERRIDDEN_BRANCH", "%reverse.dep.*.OVERRIDDEN_BRANCH%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        cleanCheckout = true
    }

    steps {
        script {
            name = "Checkout target branch for Apache Ignite repository"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                git fetch -p
                git checkout %OVERRIDDEN_BRANCH%
            """.trimIndent()
        }
        script {
            name = "Cleanup local maven repository"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                rm -rfv ~/.m2/repository/org/apache/ignite
            """.trimIndent()
        }
        maven {
            name = "Build Apache Ignite"
            goals = "install"
            pomLocation = ""
            runnerArgs = """
                -U
                -Pall-java,all-scala,scala,licenses,lgpl,examples,checkstyle
                -DskipTests
                -Dmaven.javadoc.skip=true
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        script {
            name = "Prepare built artifacts"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                mkdir -pv repository
                cp -rfv ~/.m2/repository/org/apache/ignite/* repository/
            """.trimIndent()
        }
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
        startsWith("teamcity.agent.name", "aitc")
    }
})
