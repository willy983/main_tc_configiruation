package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_Rocketmq : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "RocketMQ"

    params {
        text("DIR_EXTENSION", "rocketmq-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
