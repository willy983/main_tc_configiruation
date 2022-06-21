package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_ThirdpartyCheckout : Template({
    name = "Thirdparty Checkout"
    description = "Used to override checked out branch for external repositories"

    params {
        text("OVERRIDDEN_BRANCH", "master", label = "Branch", description = "Overridden branch for external repositories", allowEmpty = false)
        text("REPOSITORY_LIST", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "Checkout target branch in remote repositories"
            id = "RUNNER_77"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                for repository in %REPOSITORY_LIST%; do
                    cd ${'$'}{repository}
                    git fetch -p
                    git checkout %OVERRIDDEN_BRANCH%
                    cd ..
                done
            """.trimIndent()
        }
    }
})
