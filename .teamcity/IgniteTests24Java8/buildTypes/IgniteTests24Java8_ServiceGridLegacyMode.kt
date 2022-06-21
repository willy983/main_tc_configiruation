package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_ServiceGridLegacyMode : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Service Grid (Legacy Mode)"

    params {
        param("MAVEN_OPTS", "-DforkMode=always")
        text("MAVEN_MODULES", ":ignite-schedule,:ignite-jcl,:ignite-log4j,:ignite-log4j2,:ignite-slf4j,:ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("JVM_ARGS", """
            -DIGNITE_MARSHAL_BUFFERS_RECHECK=1000
            -DIGNITE_EVENT_DRIVEN_SERVICE_PROCESSOR_ENABLED=false
        """.trimIndent(), display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteServiceGridTestSuite,IgniteServiceConfigVariationsFullApiTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 60
    }
})
