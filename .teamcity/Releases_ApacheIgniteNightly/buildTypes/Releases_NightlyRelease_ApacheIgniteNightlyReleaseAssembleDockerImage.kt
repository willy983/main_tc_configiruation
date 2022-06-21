package Releases_ApacheIgniteNightly.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object Releases_NightlyRelease_ApacheIgniteNightlyReleaseAssembleDockerImage : BuildType({
    name = "[APACHE IGNITE NIGHTLY RELEASE] #3 :: Assemble Docker Image"

    artifactRules = "*.tar.gz"

    params {
        param("WEBAGENT_ARCHIVE_NAME", "web-agent-%IGNITE_VERSION%-docker-image")
        text("DOCKER_ARCHIVE_NAME", "apache-ignite-%IGNITE_VERSION%-docker-image", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("IGNITE_VERSION", "${Releases_NightlyRelease_ApacheIgniteNightlyReleasePrepare.depParamRefs["IGNITE_VERSION"]}", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("WEBCONSOLE_ARCHIVE_NAME", "web-console-standalone-%IGNITE_VERSION%-docker-image")
        text("DIR__DOCKERFILE", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        cleanCheckout = true
    }

    steps {
        script {
            name = "Prepare"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                DIR__DOCKERFILE="docker/apache-ignite"
                DIR__NEW_DOCKERFILE="deliveries/docker/apache-ignite/x86_64"
                if [ -d "${'$'}{DIR__NEW_DOCKERFILE}" ]; then
                	DIR__DOCKERFILE="${'$'}{DIR__NEW_DOCKERFILE}"
                    cp -rfv "${'$'}{DIR__DOCKERFILE}/../run.sh" "${'$'}{DIR__DOCKERFILE}"
                fi
                cp -rf apache-ignite ${'$'}{DIR__DOCKERFILE}/
                echo "##teamcity[setParameter name='DIR__DOCKERFILE' value='${'$'}{DIR__DOCKERFILE}']"
            """.trimIndent()
        }
        step {
            name = "Assemble Apache Ignite Docker image"
            type = "DockerBuildRemote"
            param("DOCKER_TAG_NAME", "apacheignite/ignite")
            param("DOCKER_ARCHIVE_NAME", "%DOCKER_ARCHIVE_NAME%")
            param("DOCKER_TAG_VERSION", "%IGNITE_VERSION%")
            param("DOCKER_SRC_DIR", "%DIR__DOCKERFILE%")
        }
    }

    dependencies {
        dependency(Releases_NightlyRelease_ApacheIgniteNightlyReleaseAssembleBinaries) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                artifactRules = "apache-ignite-%IGNITE_VERSION%-bin.zip!apache-ignite-%IGNITE_VERSION%-bin/** => apache-ignite"
            }
        }
        dependency(Releases_NightlyRelease_ApacheIgniteNightlyReleasePrepare) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                artifactRules = "apache-ignite-%IGNITE_VERSION%-src.zip!** => ."
            }
        }
    }
})
