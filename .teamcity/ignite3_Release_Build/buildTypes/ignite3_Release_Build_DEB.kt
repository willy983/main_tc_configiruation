package ignite3_Release_Build.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object ignite3_Release_Build_DEB : BuildType({
    name = "[3] DEB"

    artifactRules = "%DIR_PACKAGES% => %DIR_PACKAGES%"

    params {
        text("DIR_BUILD", "deliveries/deb", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("DIR_BINARIES", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("DIR_PACKAGES", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    steps {
        script {
            name = "Build DEB"
            workingDir = "deliveries/deb"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                bash build.sh %teamcity.build.checkoutDir%/%DIR_PACKAGES%/apache-ignite-*.noarch.rpm
            """.trimIndent()
        }
        script {
            name = "Prepare artifacts"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                mkdir -pv %DIR_PACKAGES%
                cp -rfv %DIR_BUILD%/*.deb %DIR_PACKAGES%/
            """.trimIndent()
        }
    }

    dependencies {
        dependency(ignite3_Release_Build_RPM) {
            snapshot {
            }

            artifacts {
                artifactRules = "%DIR_PACKAGES% => %DIR_PACKAGES%"
            }
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
