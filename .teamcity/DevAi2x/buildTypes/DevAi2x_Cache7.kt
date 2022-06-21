package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_Cache7 : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Cache 7"

    params {
        text("MAVEN_MODULES", ":ignite-core,:ignite-indexing", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "org.apache.ignite.testsuites.IgniteCacheTestSuite7,org.apache.ignite.testsuites.IgniteCacheWithIndexingAndPersistenceTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 90
    }
})
