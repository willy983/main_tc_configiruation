package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_ThinClientPythonOld : BuildType({
    templates(DevAi2x_ExcludeTests, DevAi2x_PreBuild, _Self.buildTypes.ThinClientStartIgnite, _Self.buildTypes.ThinClientStopIgnite, DevAi2x_PostBuild)
    name = "Thin client: Python (old)"

    artifactRules = "work/log/** => logs.zip"

    params {
        text("ACTUAL_VERSION", "%dep.DevAi2x_Build_2.ACTUAL_VERSION%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("env.PYTHON_TEST_CONFIG_PATH", "%system.teamcity.build.checkoutDir%/modules/platforms/python/tests/config")
        text("SKIP_BUILD_CONDITION", """! -d "modules/platforms/python"""", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "Run Python tests"
            id = "RUNNER_186"
            workingDir = "modules/platforms/python"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                if %system.SKIP_BUILD%; then exit 0; fi
                
                
                # Run tests
                python3 ./setup.py pytest || exit 1
            """.trimIndent()
        }
        script {
            name = "Start Ignite node with SSL"
            id = "RUNNER_45"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                if %system.SKIP_BUILD%; then exit 0; fi
                
                
                # Run Apache Ignite node
                bash %system.teamcity.build.checkoutDir%/bin/ignite.sh %env.PYTHON_TEST_CONFIG_PATH%/ssl.xml &
                sleep %IGNITE_INIT_TIME%
            """.trimIndent()
        }
        script {
            name = "Run Python tests - SSL"
            id = "RUNNER_46"
            workingDir = "modules/platforms/python"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                if %system.SKIP_BUILD%; then exit 0; fi
                
                
                # Run tests
                python3 ./setup.py pytest --addopts="--use-ssl=True --ssl-certfile=%env.PYTHON_TEST_CONFIG_PATH%/ssl/client_full.pem" || exit 1
            """.trimIndent()
        }
        script {
            name = "Run Python tests - SSL with keyfile password"
            id = "RUNNER_52"
            workingDir = "modules/platforms/python"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                if %system.SKIP_BUILD%; then exit 0; fi
                
                
                # Run tests
                python3 ./setup.py pytest --addopts="--use-ssl=True --ssl-certfile=%env.PYTHON_TEST_CONFIG_PATH%/ssl/client_with_pass_full.pem --ssl-keyfile-password=654321" || exit 1
            """.trimIndent()
        }
        script {
            name = "Stop Ignite node with SSL"
            id = "RUNNER_50"
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
        stepsOrder = arrayListOf("RUNNER_159", "RUNNER_264", "RUNNER_287", "RUNNER_3", "RUNNER_186", "RUNNER_12", "RUNNER_45", "RUNNER_46", "RUNNER_52", "RUNNER_50", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_35")
    }
})
