package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_PlatformNetLongRunning : BuildType({
    templates(DevAi2x_PreBuild, DevAi2x_RunTestSuitesNet, DevAi2x_PostBuild)
    name = "Platform .NET (Long Running)"

    params {
        param("TEST_CATEGORIES_INCLUDE_LIST", """
            LONG_TEST
            EXAMPLES_TEST
        """.trimIndent())
        param("ASSEMBLY_FILES_INCLUDE_LIST", """modules\platforms\dotnet\Apache.Ignite.Core.Tests\bin\%TARGET_RELEASE%\Apache.Ignite.Core.Tests.exe""")
        text("env.PATH", """%env.PATH%;%teamcity.tool.maven.DEFAULT%\bin""", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "Run .NET Core examples tests"
            id = "RUNNER_127"
            workingDir = "modules/platforms/dotnet"
            scriptContent = """
                @echo on
                if exist run-dotnetcore-examples.bat (
                    call run-dotnetcore-examples.bat -- --all
                )
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_32", "RUNNER_33", "RUNNER_43", "RUNNER_54", "RUNNER_71", "RUNNER_127", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 180
    }
})
