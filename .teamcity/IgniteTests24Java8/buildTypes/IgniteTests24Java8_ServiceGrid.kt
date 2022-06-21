package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_ServiceGrid : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Service Grid"

    params {
        text("MAVEN_MODULES", ":ignite-schedule,:ignite-jcl,:ignite-log4j,:ignite-log4j2,:ignite-slf4j,:ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("MAVEN_PROFILES", "all-java,all-scala,scala,all-other,compatibility,lgpl,yardstick,benchmarks,examples,surefire-fork-count-1")
        text("JVM_ARGS", "-DIGNITE_MARSHAL_BUFFERS_RECHECK=1000", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteServiceGridTestSuite,IgniteServiceConfigVariationsFullApiTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 60
    }
})
