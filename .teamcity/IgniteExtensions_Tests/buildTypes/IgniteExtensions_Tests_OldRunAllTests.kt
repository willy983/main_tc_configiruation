package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.Swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.swabra
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object IgniteExtensions_Tests_OldRunAllTests : BuildType({
    name = "~(OLD) Run All Tests"
    paused = true

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        ./dev-tools/IGNITE-*-*.patch => patch
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
    """.trimIndent()

    params {
        text("MAVEN_GOALS", "package", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("XMX", "2g", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "FlinkIgniteSinkSelfTestSuite,FlinkIgniteSourceSelfTestSuite,PubSubStreamerTestSuite,IgniteAutoconfigureTest,IgniteClientAutoConfigureTest,RocketMQStreamerTestSuite,IgniteMqttStreamerTestSuite,IgniteStormStreamerSelfTestSuite,IgniteCamelStreamerTestSuite,IgniteJmsStreamerTestSuite,IgniteKafkaStreamerSelfTestSuite,IgniteSpringData2TestSuite,IgniteSpringData22TestSuite,IgniteSpringDataTestSuite,IgnitePerformanceStatisticsReportTestSuite,IgniteRepositoriesAutoconfigureTest,IgniteClientRepositoriesAutoconfigureTest", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("EXTRA_MAVEN_PROFILES", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.JAVA_HOME", "%reverse.dep.*.env.JAVA_HOME%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        checkbox("IGNITE_LOGGING_OPTS", "-DIGNITE_TEST_PROP_LOG4J_FILE=log4j-tc-test.xml -DIGNITE_QUIET=true", label = "Quite console output",
                  checked = "-DIGNITE_TEST_PROP_LOG4J_FILE=log4j-tc-test.xml -DIGNITE_QUIET=true", unchecked = "-DIGNITE_QUIET=false")
        text("JVM_EXTRA_ARGS", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        select("reverse.dep.*.env.JAVA_HOME", "%env.JDK_ORA_8%", label = "JDK version", description = "Select JDK version for all tests",
                options = listOf("JDK 8" to "%env.JDK_ORA_8%", "JDK 9" to "%env.JDK_ORA_9%", "JDK 10" to "%env.JDK_ORA_10%", "JDK 11" to "%env.JDK_OPEN_11%"))
        text("MAVEN_OPTS", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("MAVEN_MODULES", ":ignite-flink-ext,:ignite-spring-boot-autoconfigure-ext,:ignite-spring-boot-thin-client-autoconfigure-ext,:ignite-flume-ext,:ignite-pub-sub-ext,:ignite-twitter-ext,:ignite-zeromq-ext,:ignite-rocketmq-ext,:ignite-mqtt-ext,:ignite-storm-ext,:ignite-camel-ext,:ignite-jms11-ext,:ignite-kafka-ext,:ignite-spring-data-2.0-ext,:ignite-spring-data-2.2-ext,:ignite-spring-data-ext,:ignite-performance-statistics-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("JVM_ARGS", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("TEST_SCALE_FACTOR", "1.0")
        text("XMS", "2g", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgniteExtensions)
    }

    steps {
        script {
            name = "Pre-build cleanup"
            scriptContent = """
                :<<BATCH
                
                
                :: === CMD ===
                @echo on
                exit /b
                BATCH
                
                
                # === SH ===
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
                :<<BATCH
                
                
                :: === CMD ===
                @echo on
                del /s /f /q C:\.m2\repository\org\apache\ignite
                md C:\.m2\repository\org\apache\ignite\
                xcopy ignite\repository\* C:\.m2\repository\org\apache\ignite\ /s
                exit /b
                BATCH
                
                
                # === SH ===
                set -x
                rm -rfv ~/.m2/repository/org/apache/ignite
                mkdir -pv ~/.m2/repository/org/apache/ignite
                cp -rfv ignite/repository/* ~/.m2/repository/org/apache/ignite/
            """.trimIndent()
        }
        maven {
            name = "Add JDK9+ libraries to local Maven repository (~/.m2/repository)"
            goals = "org.apache.maven.plugins:maven-dependency-plugin:2.8:get"
            pomLocation = "ignite/pom.xml"
            runnerArgs = "-Dartifact=javax.transaction:javax.transaction-api:1.3"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
        }
        maven {
            name = "Build"
            goals = "%MAVEN_GOALS%"
            runnerArgs = """
                -pl %MAVEN_MODULES% -am
                -Dmaven.test.failure.ignore=true
                -DfailIfNoTests=false
                -Dtest=%TEST_SUITE%
                -Dmaven.javadoc.skip=true
                %MAVEN_OPTS%
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
        }
        script {
            name = "Post-build cleanup"
            scriptContent = """
                :<<BATCH
                
                
                :: === CMD ===
                @echo on
                exit /b
                BATCH
                
                
                # === SH ===
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

    triggers {
        vcs {
        }
    }

    features {
        swabra {
            filesCleanup = Swabra.FilesCleanup.AFTER_BUILD
            lockingProcesses = Swabra.LockingProcessPolicy.KILL
        }
    }

    dependencies {
        artifacts(IgniteTests24Java8.buildTypes.IgniteTests24Java8_BuildApacheIgnite) {
            buildRule = lastSuccessful()
            cleanDestination = true
            artifactRules = "ignite.zip!** => ./ignite/"
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
