package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object RunPythonTestsSsl : Template({
    name = "Run Python tests (SSL)"
    description = "=> Requires Run Python tests (Basic)"

    params {
        text("env.PYTHON_TEST_CONFIG_PATH", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("IGNITE_INIT_TIME", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("VCS_ROOT_PYTHON", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "Start Ignite node with SSL"
            id = "RUNNER_91"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Run Apache Ignite node
                bash %system.teamcity.build.checkoutDir%/bin/ignite.sh %env.PYTHON_TEST_CONFIG_PATH%/ssl.xml &
                sleep %IGNITE_INIT_TIME%
            """.trimIndent()
        }
        script {
            name = "Run Python tests - SSL"
            id = "RUNNER_92"
            workingDir = "%VCS_ROOT_PYTHON%"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Run tests
                python3 ./setup.py pytest --addopts="--use-ssl=True --ssl-certfile=%env.PYTHON_TEST_CONFIG_PATH%/ssl/client_full.pem" || exit 1
            """.trimIndent()
        }
        script {
            name = "Run Python tests - SSL with keyfile password"
            id = "RUNNER_93"
            workingDir = "%VCS_ROOT_PYTHON%"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Run tests
                python3 ./setup.py pytest --addopts="--use-ssl=True --ssl-certfile=%env.PYTHON_TEST_CONFIG_PATH%/ssl/client_with_pass_full.pem --ssl-keyfile-password=654321" || exit 1
            """.trimIndent()
        }
        script {
            name = "Stop Ignite node with SSL"
            id = "RUNNER_94"
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
