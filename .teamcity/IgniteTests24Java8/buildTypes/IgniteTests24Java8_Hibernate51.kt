package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_Hibernate51 : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "~[DEPRECATED] Hibernate 5.1"

    params {
        text("MAVEN_MODULES", ":ignite-hibernate_5.1", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteHibernate5TestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 20
    }
})
