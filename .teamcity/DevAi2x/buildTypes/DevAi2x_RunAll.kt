package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_RunAll : BuildType({
    name = "--> Run :: All"
    description = "Dummy build for run all build in project by one click"

    artifactRules = "report.html"
    type = BuildTypeSettings.Type.COMPOSITE

    params {
        checkbox("reverse.dep.*.IGNITE_LOGGING_OPTS", "-DIGNITE_TEST_PROP_LOG4J_FILE=log4j-tc-test.xml -DIGNITE_QUIET=true", label = "Quite console output", display = ParameterDisplay.PROMPT,
                  checked = "-DIGNITE_TEST_PROP_LOG4J_FILE=log4j-tc-test.xml -DIGNITE_QUIET=true", unchecked = "-DIGNITE_QUIET=false")
        text("reverse.dep.*.TEST_SCALE_FACTOR", "0.1", label = "Test scale factor", allowEmpty = true)
        select("reverse.dep.*.env.JAVA_HOME", "%env.JDK_ORA_8%", label = "JDK version", description = "Select JDK version for all tests",
                options = listOf("JDK 8" to "%env.JDK_ORA_8%", "JDK 11" to "%env.JDK_OPEN_11%"))
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        excludeDefaultBranchChanges = true
        showDependenciesChanges = true
    }

    steps {
        script {
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                curl "http://172.25.5.21:8080/rest/chainResults/html?serverId=public&buildId=%teamcity.build.id%" > report.html
            """.trimIndent()
        }
    }

    dependencies {
        snapshot(DevAi2x_ActivateDeactivateCluster) {
        }
        snapshot(DevAi2x_Aop) {
        }
        snapshot(DevAi2x_Aws) {
        }
        snapshot(DevAi2x_Basic1) {
        }
        snapshot(DevAi2x_Basic2) {
        }
        snapshot(DevAi2x_Basic3) {
        }
        snapshot(DevAi2x_BinaryObjects) {
        }
        snapshot(DevAi2x_Cache1) {
        }
        snapshot(DevAi2x_Cache2) {
        }
        snapshot(DevAi2x_Cache3) {
        }
        snapshot(DevAi2x_Cache4) {
        }
        snapshot(DevAi2x_Cache5) {
        }
        snapshot(DevAi2x_Cache6) {
        }
        snapshot(DevAi2x_Cache7) {
        }
        snapshot(DevAi2x_Cache8) {
        }
        snapshot(DevAi2x_Cache9) {
        }
        snapshot(DevAi2x_CacheDeadlockDetection) {
        }
        snapshot(DevAi2x_CacheExpiryPolicy) {
        }
        snapshot(DevAi2x_CacheFailover1) {
        }
        snapshot(DevAi2x_CacheFailover2) {
        }
        snapshot(DevAi2x_CacheFailover3) {
        }
        snapshot(DevAi2x_CacheFailoverSsl) {
        }
        snapshot(DevAi2x_CacheFullApi) {
        }
        snapshot(DevAi2x_CacheFullApiMultiJvm) {
        }
        snapshot(DevAi2x_CacheRestarts1) {
        }
        snapshot(DevAi2x_CacheRestarts2) {
        }
        snapshot(DevAi2x_CacheTxRecovery) {
        }
        snapshot(DevAi2x_CassandraStore) {
        }
        snapshot(DevAi2x_ClientNodes) {
        }
        snapshot(DevAi2x_Cloud) {
        }
        snapshot(DevAi2x_ComputeAffinityRun) {
        }
        snapshot(DevAi2x_ComputeGrid) {
        }
        snapshot(DevAi2x_ContinuousQuery1) {
        }
        snapshot(DevAi2x_ContinuousQuery2) {
        }
        snapshot(DevAi2x_ContinuousQuery3) {
        }
        snapshot(DevAi2x_ContinuousQuery4) {
        }
        snapshot(DevAi2x_ControlUtility) {
        }
        snapshot(DevAi2x_ControlUtilityZookeeper) {
        }
        snapshot(DevAi2x_DataStructures) {
        }
        snapshot(DevAi2x_DevUtils) {
        }
        snapshot(DevAi2x_Examples) {
        }
        snapshot(DevAi2x_ExamplesLgpl) {
        }
        snapshot(DevAi2x_Gce) {
        }
        snapshot(DevAi2x_GeospatialIndexing) {
        }
        snapshot(DevAi2x_Hibernate42) {
        }
        snapshot(DevAi2x_Hibernate51) {
        }
        snapshot(DevAi2x_Hibernate53) {
        }
        snapshot(DevAi2x_InspectionsCore) {
        }
        snapshot(DevAi2x_InterceptorCacheFullApiConfigVariationsBasic) {
        }
        snapshot(DevAi2x_JCacheTck11) {
        }
        snapshot(DevAi2x_JavaClient) {
        }
        snapshot(DevAi2x_JdbcDriver) {
        }
        snapshot(DevAi2x_Jta) {
        }
        snapshot(DevAi2x_Kubernetes) {
        }
        snapshot(DevAi2x_Logging) {
        }
        snapshot(DevAi2x_OpenCensus) {
        }
        snapshot(DevAi2x_Pds1) {
        }
        snapshot(DevAi2x_Pds2) {
        }
        snapshot(DevAi2x_Pds3) {
        }
        snapshot(DevAi2x_Pds4) {
        }
        snapshot(DevAi2x_PdsCompatibility) {
        }
        snapshot(DevAi2x_PdsIndexing) {
        }
        snapshot(DevAi2x_PdsUnitTests) {
        }
        snapshot(DevAi2x_PlatformCCMakeLinux) {
        }
        snapshot(DevAi2x_PlatformCCMakeLinuxClang) {
        }
        snapshot(DevAi2x_PlatformCCMakeWinX64Release) {
        }
        snapshot(DevAi2x_PlatformCWinX64Release) {
        }
        snapshot(DevAi2x_PlatformNet) {
        }
        snapshot(DevAi2x_PlatformNetCoreLinux) {
        }
        snapshot(DevAi2x_PlatformNetInspections) {
        }
        snapshot(DevAi2x_PlatformNetIntegrations) {
        }
        snapshot(DevAi2x_PlatformNetLongRunning) {
        }
        snapshot(DevAi2x_PlatformNetNuGet) {
        }
        snapshot(DevAi2x_Queries1) {
        }
        snapshot(DevAi2x_Queries2) {
        }
        snapshot(DevAi2x_QueriesConfigVariations) {
        }
        snapshot(DevAi2x_Rdd) {
        }
        snapshot(DevAi2x_ScalaExamples) {
        }
        snapshot(DevAi2x_ScalaVisorConsole) {
        }
        snapshot(DevAi2x_Security) {
        }
        snapshot(DevAi2x_ServiceGrid) {
        }
        snapshot(DevAi2x_ServiceGridLegacyMode) {
        }
        snapshot(DevAi2x_Spi) {
        }
        snapshot(DevAi2x_SpiUriDeploy) {
        }
        snapshot(DevAi2x_Spring) {
        }
        snapshot(DevAi2x_StartNodes) {
        }
        snapshot(DevAi2x_Streamers) {
        }
        snapshot(DevAi2x_ThinClientJava) {
        }
        snapshot(DevAi2x_ThinClientNodeJs) {
        }
        snapshot(DevAi2x_ThinClientPhp) {
        }
        snapshot(DevAi2x_ThinClientPython) {
        }
        snapshot(DevAi2x_WebSessions) {
        }
        snapshot(DevAi2x_Yarn) {
        }
        snapshot(DevAi2x_ZooKeeper) {
        }
        snapshot(DevAi2x_ZooKeeperDiscovery1) {
        }
        snapshot(DevAi2x_ZooKeeperDiscovery2) {
        }
        snapshot(DevAi2x_ZooKeeperDiscovery3) {
        }
        snapshot(DevAi2x_ZooKeeperDiscovery4) {
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
