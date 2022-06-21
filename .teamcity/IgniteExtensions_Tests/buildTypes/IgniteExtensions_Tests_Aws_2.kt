package IgniteExtensions_Tests.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object IgniteExtensions_Tests_Aws_2 : BuildType({
    templates(IgniteExtensions_Tests_RunExtensionTests)
    name = "AWS"

    params {
        param("DIR_EXTENSION", "aws-ext")
        param("test.amazon.access.key", "test")
        param("test.amazon.secret.key", "secret")
    }
})
