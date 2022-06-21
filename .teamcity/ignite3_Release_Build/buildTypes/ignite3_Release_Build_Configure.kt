package ignite3_Release_Build.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object ignite3_Release_Build_Configure : BuildType({
    name = "[0] Configure"

    params {
        text("PROJECT_VERSION", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("PROJECT_NAME", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        cleanCheckout = true
    }

    steps {
        script {
            name = "Configure and set build chain parameters"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Get project's name and version
                PROJECT_NAME="${'$'}(xpath -e "project/artifactId/text()" pom.xml 2>/dev/null)"
                PROJECT_VERSION="${'$'}(xpath -e "project/version/text()" pom.xml 2>/dev/null)"
                
                
                
                # Set TC variables
                set +x
                echo "##teamcity[setParameter name='PROJECT_NAME' value='${'$'}{PROJECT_NAME}']"
                echo "##teamcity[setParameter name='PROJECT_VERSION' value='${'$'}{PROJECT_VERSION}']"
            """.trimIndent()
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
