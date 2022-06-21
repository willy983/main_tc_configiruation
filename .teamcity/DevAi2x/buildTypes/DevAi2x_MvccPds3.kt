package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object DevAi2x_MvccPds3 : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "MVCC PDS 3"

    params {
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("system.IGNITE_FORCE_MVCC_MODE_IN_TESTS", "true")
        text("system.DIGNITE_MARSHAL_BUFFERS_RECHECK", "1000", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "org.apache.ignite.testsuites.IgnitePdsMvccTestSuite3", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        maven {
            name = "Add JDK9+ libraries to m2"
            id = "RUNNER_225"
            goals = "org.apache.maven.plugins:maven-dependency-plugin:2.8:get"
            runnerArgs = "-Dartifact=javax.transaction:javax.transaction-api:1.3"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
        }
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
                %JVM_ARGS% %JVM_EXTRA_ARGS%
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_225", "RUNNER_265", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 60
    }
})
