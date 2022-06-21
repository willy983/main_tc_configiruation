package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_ThinClientNodeJs : BuildType({
    templates(DevAi2x_ThirdpartyCheckout, DevAi2x_PreBuild, _Self.buildTypes.RunNodeJsTests, DevAi2x_PostBuild)
    name = "Thin client: Node.js"

    artifactRules = "work/log/** => logs.zip"

    params {
        text("env.APACHE_IGNITE_CLIENT_DEBUG", "false", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.APACHE_IGNITE_CLIENT_ENDPOINTS", "127.0.0.1:10800", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.IGNITE_HOME", "%system.teamcity.build.checkoutDir%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("REPOSITORY_LIST", "%VCS_ROOT_NODEJS%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("NODEJS_VERSION", "10.16.3", label = "Node.js Version", allowEmpty = true)
        text("IGNITE_INIT_TIME", "60", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgniteNodejsThinClient, "+:. => %VCS_ROOT_NODEJS%")

        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "Install Node.js"
            id = "RUNNER_84"
            workingDir = "%VCS_ROOT_NODEJS%"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Get and install Node
                wget -c https://nodejs.org/dist/v%NODEJS_VERSION%/node-v%NODEJS_VERSION%-linux-x64.tar.xz
                tar xf node-v%NODEJS_VERSION%-linux-x64.tar.xz
                mv -v node-v%NODEJS_VERSION%-linux-x64 node
                
                
                # Install AI Node.js Thin Client module
                npm install
                npm run build
                npm link
                npm link apache-ignite-client
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_77", "RUNNER_264", "RUNNER_287", "RUNNER_84", "RUNNER_144", "RUNNER_145", "RUNNER_146", "RUNNER_147", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_35")
    }
    
    disableSettings("RUNNER_85")
})
