package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_CassandraStore : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "Cassandra Store"

    params {
        param("MAVEN_OPTS", "-DforkMode=always")
        text("MAVEN_MODULES", ":ignite-cassandra-store", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteCassandraStoreTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 20
    }
})
