package ignite3.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object ignite3_BuildApacheIgnite : BuildType({
    name = "Build :: Apache Ignite"

    artifactRules = """
        ignite.zip
        repository.zip
        target/*.zip
    """.trimIndent()

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
        showDependenciesChanges = true
    }

    steps {
        script {
            name = "Clean up local maven repository"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                rm -rfv ~/.m2/repository/org/apache/ignite
            """.trimIndent()
        }
        maven {
            name = "Build"
            goals = "install"
            pomLocation = ""
            runnerArgs = """
                -DskipTests
                -Dmaven.javadoc.skip
                -Dmaven.all-checks.skip
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_11%"
        }
        script {
            name = "Prepare artifacts"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Prepare archive with 'target' directories
                zip -r ignite {target,modules/*/target,examples/target} -x '*.jar'
                
                
                # Prepare archive with installed artifacts
                mkdir -pv repository/org/apache/ignite
                cp -rfv ~/.m2/repository/org/apache/ignite/* repository/org/apache/ignite/
                zip -r repository repository
            """.trimIndent()
        }
    }

    failureConditions {
        executionTimeoutMin = 10
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
