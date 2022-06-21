package Releases_ApacheIgniteMain.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object Releases_ApacheIgniteMain_AssembleDockerImages : BuildType({
    name = "[5] Assemble Docker Images"

    artifactRules = "*.tar.gz"

    params {
        text("IGNITE_VERSION", "", label = "Ignite version", display = ParameterDisplay.PROMPT,
              regex = "[0-9]+.[0-9]+.[0-9]+", validationMessage = "Version format: '<major>.<minor>.<fix>'")
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        cleanCheckout = true
    }

    steps {
        script {
            name = "Download Apache Ignite archive"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                wget --retry-connrefused \
                     --waitretry=1 \
                     --read-timeout=20 \
                     --timeout=15 \
                     -t 3 \
                     -c https://dlcdn.apache.org/ignite/%IGNITE_VERSION%/apache-ignite-%IGNITE_VERSION%-bin.zip
                unzip apache-ignite-%IGNITE_VERSION%-bin.zip
                mv apache-ignite-%IGNITE_VERSION%-bin apache-ignite
            """.trimIndent()
        }
        script {
            name = "Prepare context"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                DOCKER_ROOT__DIR="deliveries/docker/apache-ignite"
                for arch in s390x x86_64; do
                    cp -rf apache-ignite ${'$'}{DOCKER_ROOT__DIR}/${'$'}{arch}
                    cp -rfv ${'$'}{DOCKER_ROOT__DIR}/run.sh ${'$'}{DOCKER_ROOT__DIR}/${'$'}{arch}
                done
            """.trimIndent()
        }
        step {
            name = "Assemble Apache Ignite Docker image :: jdk8/x86_64"
            type = "DockerBuildRemote"
            param("DOCKER_TAG_NAME", "apacheignite/ignite")
            param("DOCKER_ARCHIVE_NAME", "apacheignite_ignite-%IGNITE_VERSION%")
            param("DOCKER_ARGS", "--build-arg JDK_VERSION=8")
            param("DOCKER_TAG_VERSION", "%IGNITE_VERSION%")
            param("DOCKER_SRC_DIR", "%teamcity.build.checkoutDir%/deliveries/docker/apache-ignite/x86_64")
        }
        step {
            name = "Assemble Apache Ignite Docker image :: jdk11/x86_64"
            type = "DockerBuildRemote"
            param("DOCKER_TAG_NAME", "apacheignite/ignite")
            param("DOCKER_ARCHIVE_NAME", "apacheignite_ignite-%IGNITE_VERSION%-jdk11")
            param("DOCKER_ARGS", "--build-arg JDK_VERSION=11")
            param("DOCKER_TAG_VERSION", "%IGNITE_VERSION%-jdk11")
            param("DOCKER_SRC_DIR", "%teamcity.build.checkoutDir%/deliveries/docker/apache-ignite/x86_64")
        }
        step {
            name = "Assemble Apache Ignite Docker image :: jdk11/s390x"
            type = "DockerBuildRemote"
            enabled = false
            param("DOCKER_TAG_NAME", "apacheignite/ignite")
            param("DOCKER_ARCHIVE_NAME", "apacheignite_ignite-%IGNITE_VERSION%-jdk11_s390x")
            param("DOCKER_TAG_VERSION", "%IGNITE_VERSION%-jdk11-s390x")
            param("DOCKER_SRC_DIR", "%teamcity.build.checkoutDir%/deliveries/docker/apache-ignite/s390x")
        }
    }
})
