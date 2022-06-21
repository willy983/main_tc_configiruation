package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object DevAi2x_CassandraStore : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Cassandra Store"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        ./dev-tools/IGNITE-*-*.patch => patch
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
    """.trimIndent()

    params {
        text("MAVEN_MODULES", ":ignite-cassandra-store", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteCassandraStoreTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        maven {
            name = "Run test suite"
            id = "RUNNER_265"
            goals = "%MAVEN_GOALS%"
            runnerArgs = """
                -P all-java,scala-2.10,tensorflow,scala,scala-test
                -pl %MAVEN_MODULES% -am
                -Dmaven.test.failure.ignore=true
                -DfailIfNoTests=false
                -Dtest=%TEST_SUITE%
                -Dmaven.javadoc.skip=true
                -DforkMode=always
                %MAVEN_OPTS%
            """.trimIndent()
            userSettingsPath = "settings.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jvmArgs = """
                -ea
                -server
                -Xms%XMS%
                -Xmx%XMX%
                -XX:+HeapDumpOnOutOfMemoryError
                -XX:+AggressiveOpts
                -DIGNITE_TEST_HOME="%teamcity.build.workingDir%"
                -DIGNITE_UPDATE_NOTIFIER=false
                -DIGNITE_NO_DISCO_ORDER=true
                -DIGNITE_PERFORMANCE_SUGGESTIONS_DISABLED=true
                -DIGNITE_QUIET=false
                -Djava.net.preferIPv4Stack=true
                -DTEST_SCALE_FACTOR=%TEST_SCALE_FACTOR%
                %JVM_ARGS% %JVM_EXTRA_ARGS%
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_225", "RUNNER_265", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 20
    }
})
