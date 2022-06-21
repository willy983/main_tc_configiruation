package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_ExchangeDoesnTBlockReadOperation : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Exchange doesn't block read operation"

    params {
        text("MAVEN_MODULES", ":ignite-core,:ignite-indexing", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteCacheBlockExchangeOnReadOperationsTestSuite,IgniteCacheBlockExchangeOnSqlReadOperationsTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 150
    }
})
