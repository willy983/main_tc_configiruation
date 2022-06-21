package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_Aop : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "AOP"

    params {
        text("MAVEN_MODULES", ":ignite-aop", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteAopSelfTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 30
    }
    
    disableSettings("swabra")
})
