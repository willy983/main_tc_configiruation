package IgniteExtensions_Tests

import IgniteExtensions_Tests.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("IgniteExtensions_Tests")
    name = "[TESTS]"

    buildType(IgniteExtensions_Tests_Camel)
    buildType(IgniteExtensions_Tests_Twitter)
    buildType(IgniteExtensions_Tests_Yarn)
    buildType(IgniteExtensions_Tests_Geospatial)
    buildType(IgniteExtensions_Tests_Mesos)
    buildType(IgniteExtensions_Tests_Flink)
    buildType(IgniteExtensions_Tests_Rdd)
    buildType(IgniteExtensions_Tests_Cloud)
    buildType(IgniteExtensions_Tests_Storm)
    buildType(IgniteExtensions_Tests_TopologyValidator)
    buildType(IgniteExtensions_Tests_Kafka)
    buildType(IgniteExtensions_Tests_ZeroMQ)
    buildType(IgniteExtensions_Tests_Aws_2)
    buildType(IgniteExtensions_Tests_Hibernate)
    buildType(IgniteExtensions_Tests_OldRunAllTests)
    buildType(IgniteExtensions_Tests_Checkstyle)
    buildType(IgniteExtensions_Tests_GceOld)
    buildType(IgniteExtensions_Tests_SpringTransactions)
    buildType(IgniteExtensions_Tests_Cdc)
    buildType(IgniteExtensions_Tests_PerformanceStatistics)
    buildType(IgniteExtensions_Tests_SpringBootAutoconfigure)
    buildType(IgniteExtensions_Tests_Build)
    buildType(IgniteExtensions_Tests_Mqtt)
    buildType(IgniteExtensions_Tests_RunAllTests)
    buildType(IgniteExtensions_Tests_PubSub)
    buildType(IgniteExtensions_Tests_SpringCache)
    buildType(IgniteExtensions_Tests_SpringSession)
    buildType(IgniteExtensions_Tests_Jms11)
    buildType(IgniteExtensions_Tests_Flume)
    buildType(IgniteExtensions_Tests_Gce)
    buildType(IgniteExtensions_Tests_SpringBootThinClientAutoconfigure)
    buildType(IgniteExtensions_Tests_Azure)
    buildType(IgniteExtensions_Tests_Rocketmq)
    buildType(IgniteExtensions_Tests_Aop)
    buildType(IgniteExtensions_Tests_SpringData)
    buildType(IgniteExtensions_Tests_ZookeeperIpFinder)

    template(IgniteExtensions_Tests_RunExtensionTests)
    template(IgniteExtensions_Tests_RunTestSuitesJava)
})
