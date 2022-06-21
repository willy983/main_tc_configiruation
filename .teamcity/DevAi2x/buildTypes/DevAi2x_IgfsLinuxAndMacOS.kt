package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_IgfsLinuxAndMacOS : BuildType({
    templates(DevAi2x_ExcludeTests, DevAi2x_RunTestSuitesJava)
    name = "IGFS (Linux and MacOS)"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        ./dev-tools/IGNITE-*-*.patch => patch
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
    """.trimIndent()

    params {
        text("system.hadoop.version", "2.5.2", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("MAVEN_MODULES", ":ignite-hadoop", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("SKIP_BUILD_CONDITION", "! -f modules/hadoop/pom.xml", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteIgfsLinuxAndMacOSTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 45
    }
})
