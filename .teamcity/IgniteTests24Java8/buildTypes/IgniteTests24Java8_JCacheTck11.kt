package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_JCacheTck11 : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "JCache TCK 1.1"

    params {
        param("MAVEN_OPTS", "-Djavax.cache.tck.version=1.1.0")
        param("MAVEN_GOALS", "test")
        text("MAVEN_MODULES", ":ignite-core -am", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("MAVEN_PROFILES", "!release,jcache-tck")
    }

    failureConditions {
        executionTimeoutMin = 20
        nonZeroExitCode = false
    }
    
    disableSettings("ARTIFACT_DEPENDENCY_103", "RUNNER_287")
})
