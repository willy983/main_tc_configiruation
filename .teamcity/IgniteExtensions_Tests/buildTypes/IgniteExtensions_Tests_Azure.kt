package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_Azure : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "Azure"

    params {
        text("DIR_EXTENSION", "azure-ext", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("env.test.azure.account.key", "key")
        param("env.test.azure.endpoint", "http://127.0.0.1:10000/devstoreaccount")
        param("env.test.azure.account.name", "test")
    }
})
