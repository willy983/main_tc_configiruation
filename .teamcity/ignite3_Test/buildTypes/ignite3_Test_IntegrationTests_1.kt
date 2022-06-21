package ignite3_Test.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnText

object ignite3_Test_IntegrationTests_1 : Template({
    id("ignite3_Test_IntegrationTests")
    name = "Integration Tests"

    params {
        text("JVM_ARGS", "-Xmx2g", label = "JVM ARGS", description = "Additional JavaMachine arguments to pass", allowEmpty = true)
        text("MODULE", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEXT__FOUND_JAVA_PROCESSES_ERROR", "[ERROR] Found remaining Java processes", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
        showDependenciesChanges = true
    }

    steps {
        maven {
            name = "Run tests"
            id = "RUNNER_25"
            goals = "failsafe:integration-test"
            pomLocation = ""
            runnerArgs = """
                -pl %MODULE%
                -Dmaven.test.failure.ignore=true
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_11%"
            jvmArgs = "%JVM_ARGS%"
        }
        script {
            name = "Check for remaining Java processes after tests"
            id = "RUNNER_190"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Get list of Java processes (without agent)
                PROCESSES=()
                while read string; do
                    pid="${'$'}(cut -d" " -f1 <<< "${'$'}{string}")"
                    process_name="${'$'}(cut -d" " -f5- <<< "${'$'}{string}")"
                    PROCESSES+=( "${'$'}{pid} :: ${'$'}{process_name}" )
                    kill -9 ${'$'}{pid}
                done < <(ps a | grep '[b]in/java' | grep -v '/opt/java/openjdk/jre/bin/java' | tr -s ' ' || true)
                if [ "${'$'}{#PROCESSES[@]}" -ne 0 ]; then
                    set +x; echo "%TEXT__FOUND_JAVA_PROCESSES_ERROR%:"; set -x
                    for i in "${'$'}{PROCESSES[@]}"; do
                        echo "    - ${'$'}{i}"
                    done
                fi
            """.trimIndent()
        }
    }

    failureConditions {
        executionTimeoutMin = 30
        failOnText {
            id = "BUILD_EXT_19"
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "%TEXT__FOUND_JAVA_PROCESSES_ERROR%"
            failureMessage = "%TEXT__FOUND_JAVA_PROCESSES_ERROR%"
            reverse = false
        }
        failOnText {
            id = "BUILD_EXT_23"
            conditionType = BuildFailureOnText.ConditionType.CONTAINS
            pattern = "[ERROR] Killed"
            failureMessage = "[ERROR] Killed by OOM killer"
            reverse = false
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_19")
    }
})
