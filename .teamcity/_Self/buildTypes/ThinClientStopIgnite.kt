package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object ThinClientStopIgnite : Template({
    name = "Thin Client: Stop Ignite"
    description = "Stops all Apache Ignite nodes"

    steps {
        script {
            name = "Stop Ignite node"
            id = "RUNNER_12"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Stop node by it's PID (if it was not killed previously)
                ps ax | grep ignite.sh | sed '${'$'}d' | awk {'print ${'$'}1'} | while read pid; do
                    kill -9 ${'$'}{pid} || true
                done
            """.trimIndent()
        }
    }
})
