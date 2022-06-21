package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object IgniteTests24Java8_CheckCodeStyleDucktests : BuildType({
    name = "[Check Code Style Ducktests]"

    params {
        text("env.JAVA_HOME", "%env.JDK_ORA_8%", allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    steps {
        script {
            name = "Check codestyle"
            scriptContent = """
                cd modules/ducktests/tests
                tox -e codestyle,py38 || exit 1
            """.trimIndent()
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
