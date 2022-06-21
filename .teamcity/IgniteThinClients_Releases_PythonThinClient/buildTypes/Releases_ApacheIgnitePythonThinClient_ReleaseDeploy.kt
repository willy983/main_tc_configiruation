package IgniteThinClients_Releases_PythonThinClient.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object Releases_ApacheIgnitePythonThinClient_ReleaseDeploy : BuildType({
    name = "[2] Release Deploy"

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnitePythonThinClient)
    }

    steps {
        script {
            name = "Apply release properties"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                . release.properties
                echo ${'$'}{REVISION}
                echo ${'$'}{VERSION}
                
                
                set +x
                echo "##teamcity[setParameter name='REVISION' value='${'$'}{REVISION}']"
                echo "##teamcity[setParameter name='VERSION' value='${'$'}{VERSION}']"
            """.trimIndent()
        }
        script {
            name = "Deploy to PyPI"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                if %FLAG_DEPLOY_TO_REPOSITORY%; then
                    # Publish
                    twine upload -u %PYPIORG_USER% \
                                 -p %PYPIORG_PASSWORD% \
                                 %ARTIFACTS_DIR%/{*.tar.gz,*.whl}
                fi
            """.trimIndent()
        }
        script {
            name = "Cleanup PyPI login"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                if %FLAG_DEPLOY_TO_REPOSITORY%; then
                    rm -rfv ~/.pypirc
                fi
            """.trimIndent()
        }
        script {
            name = "Deploy all"
            enabled = false
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
            """.trimIndent()
        }
        script {
            name = "Tag released code"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Change code to built revision
                if %FLAG_DEPLOY_TO_REPOSITORY%; then
                    git reset %REVISION% --hard
                
                    if [ "${'$'}(git rev-parse --verify HEAD)" == "%REVISION%" ]; then
                        # Git setup
                        git config --global user.name "GridGain Teamcity"
                        git config --global user.email ggtc@gridgain.com
                
                        # Git tag
                        git tag -d %VERSION% || true
                        git push --delete %VCS_ROOT_URL% %VERSION% || true
                        git tag -a %VERSION% -m "%VERSION% release"
                        git push %VCS_ROOT_URL% %VERSION%
                    else
                        echo "[ERROR] Unable to find revision for tagging release"
                        exit 1
                    fi
                fi
            """.trimIndent()
        }
    }

    dependencies {
        artifacts(IgniteThinClients_Releases_PythonThinClient_ReleaseBuild) {
            buildRule = lastSuccessful()
            artifactRules = """
                %ARTIFACTS_DIR% => %ARTIFACTS_DIR%
                release.properties
            """.trimIndent()
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
