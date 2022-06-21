package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.Swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_Test : BuildType({
    name = "> Test"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
        ./dev-tools/IGNITE-*-*.patch => patch
    """.trimIndent()

    params {
        text("MAVEN_GOALS", "surefire:test", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("XMX", "2g", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteAopSelfTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("EXTRA_MAVEN_PROFILES", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        select("env.JAVA_HOME", "%env.JDK_ORA_8%", label = "JDK version", description = "Select JDK version for all tests",
                options = listOf("JDK 8" to "%env.JDK_ORA_8%", "JDK 11" to "%env.JDK_OPEN_11%"))
        checkbox("IGNITE_LOGGING_OPTS", "-DIGNITE_TEST_PROP_LOG4J_FILE=log4j-tc-test.xml -DIGNITE_QUIET=false", label = "Quite console output",
                  checked = "-DIGNITE_TEST_PROP_LOG4J_FILE=log4j-tc-test.xml -DIGNITE_QUIET=true", unchecked = "-DIGNITE_QUIET=false")
        checkbox("reverse.dep.*.FLAG_RUN_SANITY_CHECKS", "true", label = "Run Sanity Checks", description = "Check Licenses, Javadoc and Code Style along the main Build", display = ParameterDisplay.PROMPT,
                  checked = "true", unchecked = "false")
        text("JVM_EXTRA_ARGS", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("MAVEN_OPTS", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("MAVEN_MODULES", ":ignite-aop", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("JVM_ARGS", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("TEST_SCALE_FACTOR", "1.0")
        text("XMS", "2g", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    steps {
        script {
            name = "Pre-build cleanup"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
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
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                cp -rfv repository ~/.m2/
            """.trimIndent()
        }
        maven {
            name = "Add JDK9+ libraries to local Maven repository (~/.m2/repository)"
            goals = "org.apache.maven.plugins:maven-dependency-plugin:2.8:get"
            runnerArgs = "-Dartifact=javax.transaction:javax.transaction-api:1.3"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        maven {
            name = "Run test suite"
            goals = "%MAVEN_GOALS%"
            runnerArgs = """
                -P %MAVEN_PROFILES%
                -pl %MAVEN_MODULES% -am
                -Dmaven.test.failure.ignore=true
                -DfailIfNoTests=false
                -Dtest=%TEST_SUITE%
                -Dmaven.javadoc.skip=true
                %MAVEN_OPTS%
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            userSettingsPath = "settings.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jvmArgs = """
                -ea
                -server
                -Xms%XMS%
                -Xmx%XMX%
                -XX:+HeapDumpOnOutOfMemoryError
                -DIGNITE_HOME=%teamcity.build.workingDir%
                -DIGNITE_TEST_HOME=%teamcity.build.workingDir%
                -DIGNITE_UPDATE_NOTIFIER=false
                -DIGNITE_NO_DISCO_ORDER=true
                -DIGNITE_PERFORMANCE_SUGGESTIONS_DISABLED=true
                -Djava.net.preferIPv4Stack=true
                -DTEST_SCALE_FACTOR=%TEST_SCALE_FACTOR%
                %IGNITE_LOGGING_OPTS%
                %JVM_ARGS%
                %JVM_EXTRA_ARGS%
            """.trimIndent()
        }
        script {
            name = "Post-build cleanup"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
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
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    features {
        swabra {
            enabled = false
            filesCleanup = Swabra.FilesCleanup.AFTER_BUILD
            forceCleanCheckout = true
            lockingProcesses = Swabra.LockingProcessPolicy.KILL
        }
    }

    dependencies {
        dependency(DevAi2x_Build) {
            snapshot {
                onDependencyFailure = FailureAction.CANCEL
                onDependencyCancel = FailureAction.CANCEL
            }

            artifacts {
                artifactRules = """
                    ignite.zip!** => .
                    repository.zip!** => .
                """.trimIndent()
            }
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
