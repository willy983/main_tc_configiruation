package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_GceOld : BuildType({
    templates(IgniteExtensions_Tests_RunTestSuitesJava)
    name = "~[DEPRECATED] GCE"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        ./dev-tools/IGNITE-*-*.patch => patch
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
    """.trimIndent()

    params {
        text("MAVEN_MODULES", ":ignite-gce", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("env.test.gce.project.name", "449058130467")
        password("env.test.gce.account.id", "credentialsJSON:f6506df2-bed3-43be-998e-5824faa81a77")
        param("env.test.gce.p12.path", "/root/.gce/gridgain-gce-key.p12")
        text("TEST_SUITE", "IgniteGCETestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 30
    }
})
