package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_CacheFullApiConfigVariationsWithKeepBinary : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Cache (Full API Config Variations / WithKeepBinary)"

    params {
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "WithKeepBinaryCacheConfigVariationsFullApiTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 240
    }
})
