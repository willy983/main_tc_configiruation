package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteTests24Java8_Cloud : BuildType({
    templates(IgniteTests24Java8_RunTestsJava)
    name = "~[DEPRECATED] Cloud"

    params {
        text("env.test.google-compute-engine.zones.list", "us-central1-a, asia-east1-a", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("MAVEN_MODULES", ":ignite-cloud", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        password("env.test.aws-ec2.secret.key", "credentialsJSON:8319abf7-4139-4554-bb2e-c97d6933b791", display = ParameterDisplay.HIDDEN)
        text("env.test.aws-ec2.regions.list", "us-east-1", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("TEST_SUITE", "IgniteCloudTestSuite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        password("env.test.google-compute-engine.access.key", "credentialsJSON:f6506df2-bed3-43be-998e-5824faa81a77", display = ParameterDisplay.HIDDEN)
        password("env.test.rackspace-cloudservers-us.access.key", "credentialsJSON:76f850d8-fc20-451f-8f82-d7194cc1c61f", display = ParameterDisplay.HIDDEN)
        password("env.test.aws-ec2.access.key", "credentialsJSON:1f36c831-421d-49c7-8411-53af46c0040a", display = ParameterDisplay.HIDDEN)
        text("env.test.google-compute-engine.secret.key", "/home/teamcity/test_keys/gridgain-gce-key.json", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        password("env.test.rackspace-cloudservers-us.secret.key", "credentialsJSON:8ca98c1f-863b-493c-a882-b89030e1d1d2", display = ParameterDisplay.HIDDEN)
        text("env.test.aws-ec2.zones.list", "us-east-1b, us-east-1e", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.test.rackspace-cloudservers-us.regions.list", "IAD,HKG", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    failureConditions {
        executionTimeoutMin = 20
    }
})
