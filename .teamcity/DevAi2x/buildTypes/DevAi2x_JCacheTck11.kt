package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object DevAi2x_JCacheTck11 : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "JCache TCK 1.1"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        ./dev-tools/IGNITE-*-*.patch => patch
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
    """.trimIndent()

    params {
        text("MAVEN_GOALS", "test", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("MAVEN_MODULES", ":ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        maven {
            name = "Run test suite* (forced JDK8)"
            id = "RUNNER_265"
            goals = "%MAVEN_GOALS%"
            runnerArgs = """
                -U
                -P !release,jcache-tck
                -pl %MAVEN_MODULES% -am
                -Dmaven.javadoc.skip=true
                -Djavax.cache.tck.version=1.1.0
            """.trimIndent()
            userSettingsPath = "settings.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
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
                %JVM_ARGS%
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_225", "RUNNER_265", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 20
    }
})
