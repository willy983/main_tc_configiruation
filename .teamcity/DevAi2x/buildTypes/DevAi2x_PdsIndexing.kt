package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_PdsIndexing : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "PDS (Indexing)"

    artifactRules = """
        work/log => logs.zip
        work/archive_db => db.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
        ./dev-tools/IGNITE-*-*.patch => patch
    """.trimIndent()

    params {
        text("MAVEN_MODULES", ":ignite-indexing", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("env.IGNITE_WORK_DIR_ignore", "/data/teamcity/tmpfs/work")
        text("JVM_ARGS", "-DIGNITE_MARSHAL_BUFFERS_RECHECK=1000", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgnitePdsWithIndexingCoreTestSuite,IgnitePdsWithIndexingTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 180
    }
})
