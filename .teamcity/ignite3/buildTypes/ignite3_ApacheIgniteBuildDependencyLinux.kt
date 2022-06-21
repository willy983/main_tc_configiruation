package ignite3.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object ignite3_ApacheIgniteBuildDependencyLinux : Template({
    name = "Apache Ignite Build Dependency (Linux)"

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
        showDependenciesChanges = true
    }

    steps {
        script {
            name = "Import Apache Ignite artifacts"
            id = "RUNNER_175"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Install artifacts to local repository
                rm -rf ~/.m2/repository/org/apache/ignite
                cp -rfv repository ~/.m2/
            """.trimIndent()
        }
    }

    dependencies {
        dependency(ignite3_BuildApacheIgnite) {
            snapshot {
            }

            artifacts {
                id = "ARTIFACT_DEPENDENCY_10"
                artifactRules = """
                    ignite.zip!** => .
                    repository.zip!** => .
                """.trimIndent()
            }
        }
    }
})
