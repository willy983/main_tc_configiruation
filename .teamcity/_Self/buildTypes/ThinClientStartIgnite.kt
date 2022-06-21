package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object ThinClientStartIgnite : Template({
    name = "Thin Client: Start Ignite"
    description = "Runs single node of Apache Ignite"

    params {
        text("env.APACHE_IGNITE_CLIENT_DEBUG", "false", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.APACHE_IGNITE_CLIENT_ENDPOINTS", "127.0.0.1:10800", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.IGNITE_HOME", "%system.teamcity.build.checkoutDir%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("IGNITE_INIT_TIME", "60", label = "Ignite Startup Time (s)", description = "Time to wait for Ignite node to start", allowEmpty = true)
    }

    steps {
        script {
            name = "Start Ignite node"
            id = "RUNNER_3"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Run Apache Ignite node
                bash %env.IGNITE_HOME%/bin/ignite.sh &
                sleep %IGNITE_INIT_TIME%
            """.trimIndent()
        }
    }
})
