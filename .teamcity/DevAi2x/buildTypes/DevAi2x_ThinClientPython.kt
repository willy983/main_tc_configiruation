package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_ThinClientPython : BuildType({
    templates(DevAi2x_ThirdpartyCheckout, DevAi2x_PreBuild, _Self.buildTypes.RunPythonTestsBasic, _Self.buildTypes.RunPythonTestsSsl, DevAi2x_PostBuild)
    name = "Thin client: Python"

    artifactRules = "work/log/** => logs.zip"

    params {
        text("env.APACHE_IGNITE_CLIENT_DEBUG", "false", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.APACHE_IGNITE_CLIENT_ENDPOINTS", "127.0.0.1:10800", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.IGNITE_HOME", "%system.teamcity.build.checkoutDir%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("REPOSITORY_LIST", "%VCS_ROOT_PYTHON%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("IGNITE_INIT_TIME", "60", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnitePythonThinClient, "+:. => %VCS_ROOT_PYTHON%")

        checkoutMode = CheckoutMode.ON_AGENT
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_35")
    }
    
    disableSettings("RUNNER_91", "RUNNER_92", "RUNNER_93", "RUNNER_94")
})
