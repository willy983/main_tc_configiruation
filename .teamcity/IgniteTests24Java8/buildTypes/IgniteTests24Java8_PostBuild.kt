package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object IgniteTests24Java8_PostBuild : Template({
    name = "Post-build"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
    """.trimIndent()

    vcs {
        cleanCheckout = true
    }

    steps {
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
                if %system.SKIP_BUILD%; then exit 0; fi
                
                
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
    }
})
