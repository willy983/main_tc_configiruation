package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_Basic2 : BuildType({
    templates(DevAi2x_RunTestSuitesJava)
    name = "Basic 2"

    params {
        text("JVM_ARGS", "-DIGNITE_MARSHAL_BUFFERS_RECHECK=1000", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("MAVEN_MODULES", ":ignite-schedule,:ignite-jcl,:ignite-log4j,:ignite-log4j2,:ignite-slf4j,:ignite-core", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteIpcTestSuite,IgniteSchedulerTestSuite,IgniteJclTestSuite,IgniteLog4jTestSuite,IgniteLog4j2TestSuite,IgniteSlf4jTestSuite,IgniteMessagingConfigVariationFullApiTestSuite,IgniteComputeBasicConfigVariationsFullApiTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 60
    }
})
