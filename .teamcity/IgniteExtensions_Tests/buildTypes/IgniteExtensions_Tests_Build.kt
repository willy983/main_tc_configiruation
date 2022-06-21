package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object IgniteExtensions_Tests_Build : BuildType({
    name = "<Build>"

    artifactRules = "%DIR_REPOSITORY% => repository.zip"

    params {
        text("DIR_REPOSITORY", "repository", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("OVERRIDDEN_BRANCH", "%teamcity.build.branch%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        cleanCheckout = true
    }

    steps {
        script {
            name = "Cleanup local maven repository"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                rm -rfv ~/.m2/repository/org/apache/ignite
            """.trimIndent()
        }
        script {
            name = "Checkout target Ignite's branch"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                git fetch -p
                git checkout %OVERRIDDEN_BRANCH%
            """.trimIndent()
        }
        maven {
            name = "Build Apache Ignite"
            goals = "install"
            pomLocation = ""
            runnerArgs = """
                -Pall-java,all-scala,compatibility,yardstick,benchmarks,scala,spark-2.4,scala-2.10,examples
                -DskipTests
                -Dmaven.javadoc.skip=true
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        script {
            name = "Prepare artifacts"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                mkdir -pv %DIR_REPOSITORY%
                cp -rfv ~/.m2/repository/org/apache/ignite/* %DIR_REPOSITORY%/
            """.trimIndent()
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
