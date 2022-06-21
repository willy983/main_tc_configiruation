package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object IgniteTests24Java8_WiPThinClientPython : BuildType({
    templates(IgniteTests24Java8_ThirdpartyCheckout, IgniteTests24Java8_PreBuild, _Self.buildTypes.RunPythonTestsBasic, _Self.buildTypes.RunPythonTestsSsl, IgniteTests24Java8_PostBuild)
    name = "Thin client: Python"

    artifactRules = "work/log/** => logs.zip"

    params {
        text("env.APACHE_IGNITE_CLIENT_DEBUG", "false", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.APACHE_IGNITE_CLIENT_ENDPOINTS", "127.0.0.1:10800", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.IGNITE_HOME", "%system.teamcity.build.checkoutDir%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("REPOSITORY_LIST", "%VCS_ROOT_PYTHON%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("IGNITE_INIT_TIME", "60", label = "Ignite Startup Time (s)", description = "Time to wait for Ignite node to start", allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnitePythonThinClient, "+:. => %VCS_ROOT_PYTHON%")

        checkoutMode = CheckoutMode.ON_AGENT
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
                #python3 ./setup.py pytest || exit 1
                tox -e codestyle,py38 || exit 1
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_77", "RUNNER_264", "RUNNER_287", "RUNNER_90", "RUNNER_91", "RUNNER_92", "RUNNER_93", "RUNNER_94", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_35")
    }
    
    disableSettings("RUNNER_91", "RUNNER_92", "RUNNER_93", "RUNNER_94")
})
