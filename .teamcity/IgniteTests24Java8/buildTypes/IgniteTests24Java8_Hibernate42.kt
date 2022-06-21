package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_Hibernate42 : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "~[DEPRECATED] Hibernate 4.2"

    params {
        text("MAVEN_MODULES", ":ignite-hibernate_4.2", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteHibernateTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 20
    }
})
