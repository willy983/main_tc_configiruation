package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object RunPythonTestsBasic : Template({
    name = "Run Python tests (Basic)"
    description = "Runs Python tests"

    params {
        text("env.PYTHON_TEST_CONFIG_PATH", "%system.teamcity.build.checkoutDir%/%VCS_ROOT_PYTHON%/tests/config", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("VCS_ROOT_PYTHON", "ignite-python-thin-client", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "Run Python tests"
            id = "RUNNER_90"
            workingDir = "%VCS_ROOT_PYTHON%"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Run tests
                python3 ./setup.py pytest || exit 1
            """.trimIndent()
        }
    }
})
