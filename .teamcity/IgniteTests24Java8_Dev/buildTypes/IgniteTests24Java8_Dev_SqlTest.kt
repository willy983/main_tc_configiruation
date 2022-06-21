package IgniteTests24Java8_Dev.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object IgniteTests24Java8_Dev_SqlTest : BuildType({
    templates(IgniteTests24Java8.buildTypes.IgniteTests24Java8_RunTestSuitesJavaOld)
    name = "SQL Test"

    artifactRules = """
        sqltest/out_tc.xml
        sqltest/dbs/ignite/v/result/2016.json
    """.trimIndent()

    params {
        param("XMX", "5g")
        text("env.IGNITE_HOME", "%teamcity.build.checkoutDir%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "Run custom SQL tests"
            id = "RUNNER_76"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Checkout tests code
                git clone https://github.com/zstan/sqltest
                cd sqltest
                git checkout ignite
                
                
                # Configure tests
                sed -r "s|/home/zstan.*(\"\))|${'$'}(ls %teamcity.build.checkoutDir%/modules/core/target/ignite-core-*.jar | grep -vE '(sources|tests)')\1|" \
                    -i dbs/ignite/v/__init__.py
                    
                    
                # Run Apache Ignite node
                JVM_OPTS="-Xms1g -Xmx4g" bash %env.IGNITE_HOME%/bin/ignite.sh ../examples/config/example-sql.xml &
                sleep 60
                
                
                # Run tests
                python3 generate_tests.py dbs.ignite.v
                
                
                # Stop Apache Ignite node
                ps ax | grep ignite.sh | sed '${'$'}d' | awk {'print ${'$'}1'} | while read pid; do
                    kill -9 ${'$'}{pid} || true
                done
                
                
                # Parse results
            """.trimIndent()
        }
        script {
            name = "Parce JUnit test reports"
            id = "RUNNER_100"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                
                
                echo "##teamcity[importData type='surefire' file='sqltest/out_tc.xml' verbose='true' parseOutOfDate='true']"
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_225", "RUNNER_265", "RUNNER_76", "RUNNER_266", "RUNNER_100")
    }

    failureConditions {
        executionTimeoutMin = 60
    }
    
    disableSettings("RUNNER_265", "swabra")
})
