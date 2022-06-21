package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_ExcludeTests : Template({
    name = "Exclude tests"

    params {
        text("SKIP_BUILD_MAVEN_MODULE", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("SKIP_BUILD_CONDITION", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "[OVERRIDE] Set SKIP_BUILD flag if required"
            id = "RUNNER_159"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                if [ %SKIP_BUILD_CONDITION% ]; then
                    set +x
                    echo "##teamcity[setParameter name='system.SKIP_BUILD' value='true']"
                    set -x
                fi
            """.trimIndent()
        }
        script {
            name = "[OVERRIDE] Set build steps to skip if required"
            id = "RUNNER_172"
            enabled = false
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                if %system.SKIP_BUILD%; then
                    set +x
                    echo "##teamcity[setParameter name='MAVEN_JDK9_GOALS' value='clean']"
                    echo "##teamcity[setParameter name='MAVEN_BUILD_GOALS' value='clean']"
                    echo "##teamcity[setParameter name='MAVEN_TESTS_GOALS' value='clean']"
                    echo "##teamcity[setParameter name='MAVEN_MODULES' value='%SKIP_BUILD_MAVEN_MODULE%']"
                    set -x
                fi
            """.trimIndent()
        }
    }
})
