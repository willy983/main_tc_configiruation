package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_PlatformNetIntegrations : BuildType({
    templates(DevAi2x_PreBuild, DevAi2x_RunTestSuitesNet, DevAi2x_PostBuild)
    name = "Platform .NET (Integrations)"

    params {
        param("ASSEMBLY_FILES_INCLUDE_LIST", """
            modules\platforms\dotnet\Apache.Ignite.AspNet.Tests\bin\%TARGET_RELEASE%\Apache.Ignite.AspNet.Tests.dll
            modules\platforms\dotnet\Apache.Ignite.EntityFramework.Tests\bin\%TARGET_RELEASE%\Apache.Ignite.EntityFramework.Tests.dll
        """.trimIndent())
    }

    failureConditions {
        executionTimeoutMin = 120
    }
})
