package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_JdbcDriver : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "JDBC Driver"

    params {
        text("MAVEN_MODULES", ":ignite-clients", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteJdbcDriverTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("XMX", "4g", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 60
    }
})
