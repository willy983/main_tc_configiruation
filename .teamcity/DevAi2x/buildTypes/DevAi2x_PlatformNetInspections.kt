package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.fxCop
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.reSharperInspections
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnMetricChange

object DevAi2x_PlatformNetInspections : BuildType({
    templates(DevAi2x_PreBuild, DevAi2x_RunTestSuitesNet, DevAi2x_PostBuild)
    name = "Platform .NET (Inspections)*"

    steps {
        fxCop {
            name = "FxCop static analysis"
            id = "RUNNER_125"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            inspectionSource = project {
                projectFile = """modules\platforms\dotnet\Apache.Ignite.FxCop"""
            }
            failOnAnalysisError = false
        }
        reSharperInspections {
            name = "ReSharper Inspections"
            id = "RUNNER_126"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            solutionPath = "modules/platforms/dotnet/Apache.Ignite.sln"
            projectFilter = """
                Apache.Ignite.Core
                Apache.Ignite.Linq
                Apache.Ignite.AspNet
                Apache.Ignite.EntityFramework
            """.trimIndent()
            targetDotNetFramework_4_0 = true
            cltPath = "%teamcity.tool.jetbrains.resharper-clt.bundled%"
            customSettingsProfilePath = "modules/platforms/dotnet/Apache.Ignite.sln.TeamCity.DotSettings"
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_32", "RUNNER_33", "RUNNER_43", "RUNNER_54", "RUNNER_71", "RUNNER_125", "RUNNER_126", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 120
        failOnMetricChange {
            id = "BUILD_EXT_3"
            metric = BuildFailureOnMetric.MetricType.INSPECTION_ERROR_COUNT
            threshold = 0
            units = BuildFailureOnMetric.MetricUnit.DEFAULT_UNIT
            comparison = BuildFailureOnMetric.MetricComparison.MORE
            compareTo = value()
            param("anchorBuild", "lastSuccessful")
        }
        failOnMetricChange {
            id = "BUILD_EXT_4"
            metric = BuildFailureOnMetric.MetricType.INSPECTION_WARN_COUNT
            threshold = 0
            units = BuildFailureOnMetric.MetricUnit.DEFAULT_UNIT
            comparison = BuildFailureOnMetric.MetricComparison.MORE
            compareTo = value()
            param("anchorBuild", "lastSuccessful")
        }
    }
    
    disableSettings("ARTIFACT_DEPENDENCY_103", "RUNNER_32", "RUNNER_54", "RUNNER_71")
})
