package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object IgniteExtensions_Tests_RunExtensionTests : Template({
    name = "Run Extension Tests"

    params {
        text("DIR_EXTENSION", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("DIR_REPOSITORY", "${IgniteExtensions_Tests_Build.depParamRefs["DIR_REPOSITORY"]}", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("reverse.dep.*.OVERRIDDEN_BRANCH", "master", label = "Branch", description = "Overridden branch for Apache Ignite repository", allowEmpty = false)
        text("IGNITE_VERSION", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgniteExtensions)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    steps {
        script {
            name = "Install build artifacts to local Maven repository"
            id = "RUNNER_140"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                rm -rf ~/.m2/repository/org/apache/ignite
                mkdir -pv ~/.m2/repository/org/apache/ignite
                cp -rfv repository/* ~/.m2/repository/org/apache/ignite/
            """.trimIndent()
        }
        script {
            name = "Set Ignite's version"
            id = "RUNNER_143"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                IGNITE_VERSION="${'$'}(basename ${'$'}(ls -d ~/.m2/repository/org/apache/ignite/apache-ignite/*/))"
                
                
                set +x
                echo "##teamcity[setParameter name='IGNITE_VERSION' value='${'$'}{IGNITE_VERSION}']"
            """.trimIndent()
        }
        maven {
            name = "Run Extension's tests"
            id = "RUNNER_141"
            goals = "test"
            pomLocation = ""
            runnerArgs = """
                -pl modules/%DIR_EXTENSION% -am
                -Dmaven.test.failure.ignore=true
                -DfailIfNoTests=false
                -Dignite.version=%IGNITE_VERSION%
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
    }

    dependencies {
        dependency(IgniteExtensions_Tests_Build) {
            snapshot {
            }

            artifacts {
                id = "ARTIFACT_DEPENDENCY_9"
                artifactRules = "%DIR_REPOSITORY%.zip!** => repository"
            }
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_9")
    }
})
