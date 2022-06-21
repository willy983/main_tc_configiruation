package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_CalciteSql : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Calcite SQL"
    description = "Run Calcite-based SQL engine tests"

    params {
        text("MAVEN_MODULES", ":ignite-calcite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteCalciteTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
