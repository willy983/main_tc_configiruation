package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_InterceptorCacheFullApiConfigVariationsBasic : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Interceptor Cache (Full API Config Variations / Basic)*"

    params {
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "InterceptorCacheConfigVariationsFullApiTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 240
    }

    requirements {
        doesNotEqual("system.agent.name", "publicagent09_9096", "RQ_24")
    }
})
