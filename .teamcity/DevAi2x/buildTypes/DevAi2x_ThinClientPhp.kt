package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_ThinClientPhp : BuildType({
    templates(DevAi2x_ThirdpartyCheckout, DevAi2x_PreBuild, _Self.buildTypes.ThinClientStartIgnite, _Self.buildTypes.RunPhpTests, _Self.buildTypes.ThinClientStopIgnite, DevAi2x_PostBuild)
    name = "Thin client: PHP"

    artifactRules = "work/log/** => logs.zip"

    params {
        text("REPOSITORY_LIST", "%VCS_ROOT_PHP%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnitePhpThinClient, "+:. => %VCS_ROOT_PHP%")

        checkoutMode = CheckoutMode.ON_AGENT
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_35")
    }
})
