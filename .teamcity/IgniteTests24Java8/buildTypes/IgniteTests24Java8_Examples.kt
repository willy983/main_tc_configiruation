package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_Examples : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Examples"

    params {
        param("EXTRA_MAVEN_PROFILES", "-P scala-test")
        text("MAVEN_MODULES", ":ignite-examples", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("JVM_ARGS", """
            -Djava.awt.headless=true
            -Dawt.toolkit=sun.awt.HToolkit
        """.trimIndent(), display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteExamplesSelfTestSuite,IgniteExamplesSparkSelfTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 90
    }

    dependencies {
        artifacts(IgniteTests24Java8_Build) {
            id = "ARTIFACT_DEPENDENCY_15"
            artifactRules = "examples.zip!** => ."
            enabled = false
        }
    }
})
