package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_Mqtt : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "MQTT"

    params {
        text("DIR_EXTENSION", "mqtt-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }
})
