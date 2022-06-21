package IgniteThinClients_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object IgniteThinClients_Tests_ThinClientPython : BuildType({
    templates(_Self.buildTypes.Tests_IgniteThinClients_PreBuild, _Self.buildTypes.RunPythonTestsBasic, _Self.buildTypes.Tests_IgniteThinClients_PostBuild)
    name = "Thin client: Python"

    artifactRules = "work/log/** => logs.zip"

    params {
        text("env.APACHE_IGNITE_CLIENT_DEBUG", "false", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.APACHE_IGNITE_CLIENT_ENDPOINTS", "127.0.0.1:10800", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.IGNITE_HOME", "%system.teamcity.build.checkoutDir%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("IGNITE_INIT_TIME", "60", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnitePythonThinClient, "+:. => %VCS_ROOT_PYTHON%")

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "Pre-build cleanup"
            id = "RUNNER_264"
            scriptContent = """
                :<<BATCH
                
                
                :: ==================== CMD =====================
                @echo on
                exit /b
                BATCH
                
                
                #  ==================== SH ======================
                set -x
                
                
                # Pre-clean info
                echo "User: ${'$'}(whoami)"
                echo "JPS (before): "
                for process in ${'$'}(%env.JAVA_HOME%/bin/jps)
                do
                    echo "    ${'$'}{process}"
                done
                echo
                
                # Kill processes
                echo "Killing processes starters"
                for processName in GridHadoopExternalProcessStarter HadoopExternalProcessStarter MainWithArgsInFile
                do
                    for PID in ${'$'}(%env.JAVA_HOME%/bin/jps | grep ${'$'}{processName} | awk '{ print ${'$'}1 }')
                    do
                        echo -n "    Killing ${'$'}{processName} process with PID ${'$'}{PID}... "
                        processInfo="${'$'}(ps aux -p ${'$'}PID)"
                        kill -9 ${'$'}{PID} && echo "[OK]" || {
                            echo "[ERROR] Unable to kill process ${'$'}{PID}" && exit 1
                        }
                        echo "        Killed process info: ${'$'}{processInfo}"
                    done
                done
                echo
                echo "Killing IgniteNodeRunner processes (before tests)"
                for PID in ${'$'}(%env.JAVA_HOME%/bin/jps | grep IgniteNodeRunner | awk '{ print ${'$'}1 }')
                do
                    echo -n "    Killing process with PID ${'$'}{PID}... "
                    kill -9 ${'$'}{PID} && echo "[OK]" || {
                        echo "[ERROR] Unable to kill process ${'$'}{PID}" && exit 1
                    }
                done
                echo
                
                # Post-clean info
                echo "JPS (after): "
                for process in ${'$'}(%env.JAVA_HOME%/bin/jps)
                do
                    echo "    ${'$'}{process}"
                done
                echo
                
                # ULimit
                ulimit -n 65535 && echo "Max number of OPEN FILE DESCRIPTORS:           ${'$'}(ulimit -n)"
                ulimit -u 65535 && echo "Max number of SINGLE USER AVAILABLE PROCESSES: ${'$'}(ulimit -u)"
                echo
                
                # Finalize IPC cleaning
                echo "Cleaning IPC resources"
                for param in m s
                do
                    for ipcs in ${'$'}(ipcs -${'$'}{param} | grep 'teamcity' | awk '{ print ${'$'}2 }')
                    do
                        ipcrm -${'$'}{param} ${'$'}{ipcs} || {
                            echo "[ERROR] Unable to remove ${'$'}{param}/${'$'}{ipcs}" && exit 1
                        }
                    done
                done
            """.trimIndent()
        }
        script {
            name = "Install built artifacts to local maven repository"
            id = "RUNNER_287"
            scriptContent = """
                :<<BATCH
                
                
                :: ==================== CMD =====================
                @echo on
                del /s /f /q %M2_REPOSITORY_DIR_WIN%\org\apache\ignite
                md %M2_REPOSITORY_DIR_WIN%\org\apache\ignite
                xcopy repository\* %M2_REPOSITORY_DIR_WIN%\org\apache\ignite\ /s
                exit /b
                BATCH
                
                
                #  ==================== SH ======================
                set -x
                
                
                rm -rf %M2_REPOSITORY_DIR_LIN%/org/apache/ignite
                mkdir -p %M2_REPOSITORY_DIR_LIN%/org/apache/ignite
                cp -rf repository/* %M2_REPOSITORY_DIR_LIN%/org/apache/ignite/
            """.trimIndent()
        }
        script {
            name = "Run Python tests"
            id = "RUNNER_90"
            workingDir = "%VCS_ROOT_PYTHON%"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Run tests
                #python3 ./setup.py pytest --addopts="--examples" || exit 1
                tox -e codestyle,py38 || exit 1
            """.trimIndent()
        }
        script {
            name = "Start Ignite node with SSL"
            id = "RUNNER_91"
            enabled = false
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
            enabled = false
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
            name = "Stop Ignite node with SSL"
            id = "RUNNER_94"
            enabled = false
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
        script {
            name = "Post-build cleanup"
            id = "RUNNER_266"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptContent = """
                :<<BATCH
                
                
                :: ==================== CMD =====================
                @echo on
                exit /b
                BATCH
                
                
                #  ==================== SH ======================
                set -x
                
                
                # Kill remaining nodes
                echo "Killing CommandLineStartup processes"
                for PID in ${'$'}(%env.JAVA_HOME%/bin/jps | grep CommandLineStartup | awk '{ print ${'$'}1 }')
                do
                    echo -n "    Killing remaining node process with PID ${'$'}{PID}... "
                    kill -9 ${'$'}{PID} && echo "[OK]" || {
                        echo "[ERROR] Unable to kill process ${'$'}{PID}" && exit 1
                    }
                done
                echo
                
                # Finalize IPC cleaning
                echo "Cleaning IPC resources"
                for param in m s
                do
                    for ipcs in ${'$'}(ipcs -${'$'}{param} | grep 'teamcity' | awk '{ print ${'$'}2 }')
                    do
                        ipcrm -${'$'}{param} ${'$'}{ipcs} || {
                            echo "[ERROR] Unable to remove ${'$'}{param}/${'$'}{ipcs}" && exit 1
                        }
                    done
                done
                echo
                
                # Fail build if some IPC still remains
                if [ ${'$'}(ipcs | grep teamcity | wc -l) -ne 0 ]
                then
                    echo "[ERROR] Failing build because of remaining IPC resources" >&2
                    exit 1
                fi
                
                # Kill IgniteNodeRunner
                echo "Killing IgniteNodeRunner processes (after tests)"
                for PID in ${'$'}(%env.JAVA_HOME%/bin/jps | grep IgniteNodeRunner | awk '{ print ${'$'}1 }')
                do
                    echo -n "    Killing process with PID ${'$'}{PID}... "
                    kill -9 ${'$'}{PID} && echo "[OK]" || {
                        echo "[ERROR] Unable to kill process ${'$'}{PID}" && exit 1
                    }
                done
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_90", "RUNNER_91", "RUNNER_92", "RUNNER_94", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_35")
    }
})
