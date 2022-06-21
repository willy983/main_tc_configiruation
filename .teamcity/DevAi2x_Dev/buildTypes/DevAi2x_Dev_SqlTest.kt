package DevAi2x_Dev.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_Dev_SqlTest : BuildType({
    templates(DevAi2x.buildTypes.DevAi2x_RunTestSuitesJava)
    name = "SQL Test"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        sqltest/dbs/ignite/v/result/2016.json
    """.trimIndent()

    params {
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
                bash %env.IGNITE_HOME%/bin/ignite.sh &
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
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_225", "RUNNER_265", "RUNNER_76", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 30
    }
    
    disableSettings("RUNNER_265", "swabra")
})
