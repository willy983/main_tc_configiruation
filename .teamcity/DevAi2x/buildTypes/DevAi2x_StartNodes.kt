package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_StartNodes : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Start Nodes"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        ./dev-tools/IGNITE-*-*.patch => patch
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
    """.trimIndent()

    params {
        text("MAVEN_MODULES", ":ignite-ssh", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.test.ssh.password", "teamcity", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteStartStopRestartTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.test.ssh.username", "teamcity", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 30
    }
})
