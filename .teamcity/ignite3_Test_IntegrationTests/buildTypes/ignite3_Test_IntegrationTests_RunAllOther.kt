package ignite3_Test_IntegrationTests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object ignite3_Test_IntegrationTests_RunAllOther : BuildType({
    templates(ignite3.buildTypes.ignite3_ApacheIgniteBuildDependencyLinux, ignite3_Test.buildTypes.ignite3_Test_IntegrationTests_1)
    name = "> Run :: All Other"

    params {
        password("TOKEN", "credentialsJSON:a249b43b-6b39-42a6-b0fd-fae01f07ef7b", display = ParameterDisplay.HIDDEN)
        text("MODULE", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "Get list of modules to exclude"
            id = "RUNNER_104"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                MODULES=()
                URL="https://ci.ignite.apache.org/app/rest"
                AUTH="Authorization: Bearer %TOKEN%"
                
                
                function join_by {
                    local IFS="${'$'}{1}"
                    shift
                    echo "${'$'}*"
                }
                
                
                while read tag; do
                    id="${'$'}(xpath -q -e 'string(buildType/@id)' <<< "${'$'}{tag}")"
                    module="${'$'}(curl -s --header "${'$'}{AUTH}" "${'$'}{URL}/buildTypes/id:${'$'}{id}" | \
                                  xpath -q -e "string(buildType/parameters/property[@name='MODULE']/@value)")"
                    MODULES+=("-${'$'}{module}")
                done < <(curl -s --header "${'$'}{AUTH}" "${'$'}{URL}/projects/id:ignite3_Test_IntegrationTests" | \
                           xpath -q -e "project/buildTypes/buildType[contains(@name, '[Module]')]")
                MODULE="${'$'}(join_by "," ${'$'}{MODULES[@]})"
                
                
                
                set +x
                echo "##teamcity[setParameter name='MODULE' value='${'$'}{MODULE}']"
            """.trimIndent()
        }
        script {
            name = "[TEMP] Zip dump to reduce download sizes"
            id = "RUNNER_119"
            enabled = false
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                if [ -f modules/raft/dump.hprof ]; then
                	cd modules/raft
                    zip -r dump.zip dump.hprof
                    mv dump.zip ../../
                fi
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_104", "RUNNER_175", "RUNNER_25", "RUNNER_119", "RUNNER_190")
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            enabled = false
        }
    }
})
