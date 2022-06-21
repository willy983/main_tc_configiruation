package IgniteThinClients_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object IgniteThinClients_Tests_ThinClientPhp : BuildType({
    templates(_Self.buildTypes.Tests_IgniteThinClients_PreBuild, _Self.buildTypes.ThinClientStartIgnite, _Self.buildTypes.RunPhpTests, _Self.buildTypes.ThinClientStopIgnite, _Self.buildTypes.Tests_IgniteThinClients_PostBuild)
    name = "Thin client: PHP"

    artifactRules = "work/log/** => logs.zip"

    params {
        text("VCS_ROOT_PHP", "ignite-php-thin-client", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnitePhpThinClient, "+:. => %VCS_ROOT_PHP%")

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "Start Ignite node"
            id = "RUNNER_3"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                if %system.SKIP_BUILD%; then exit 0; fi
                
                
                # Run Apache Ignite node
                bash %system.teamcity.build.checkoutDir%/bin/ignite.sh &
                sleep %IGNITE_INIT_TIME%
            """.trimIndent()
        }
        script {
            name = "Stop Ignite node"
            id = "RUNNER_12"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                if %system.SKIP_BUILD%; then exit 0; fi
                
                
                # Stop node by it's PID (if it was not killed previously)
                ps ax | grep ignite.sh | sed '${'$'}d' | awk {'print ${'$'}1'} | while read pid; do
                    kill -9 ${'$'}{pid} || true
                done
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_3", "RUNNER_87", "RUNNER_88", "RUNNER_12", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_35")
    }
})
