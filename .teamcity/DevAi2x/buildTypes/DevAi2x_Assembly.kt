package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven

object DevAi2x_Assembly : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "[Assembly]"

    artifactRules = "target/bin/*"

    params {
        text("MAVEN_MODULES", ":ignite-core,:ignite-indexing", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "org.apache.ignite.testsuites.IgniteCacheTestSuite7,org.apache.ignite.testsuites.IgniteCacheWithIndexingAndPersistenceTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        maven {
            name = "[TEMP] Install tools (to be removed after IGNITE-6727)"
            id = "RUNNER_8"
            goals = "install"
            runnerArgs = "-pl modules/tools -am"
        }
        maven {
            name = "Assembly"
            id = "RUNNER_7"
            goals = "initialize"
            runnerArgs = "-Prelease"
            jdkHome = "%env.JDK_ORA_8%"
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_225", "RUNNER_265", "RUNNER_8", "RUNNER_7", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 90
    }
    
    disableSettings("RUNNER_265")
})
