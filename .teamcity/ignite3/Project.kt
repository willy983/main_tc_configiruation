package ignite3

import ignite3.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.nuGetFeed

object Project : Project({
    id("ignite3")
    name = "[Apache Ignite 3.x]"

    buildType(ignite3_BuildApacheIgnite)

    template(ignite3_ApacheIgniteBuildDependencyWindows)
    template(ignite3_ApacheIgniteBuildDependencyLinux)

    params {
        text("env.JAVA_HOME", "%env.JDK_ORA_11%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.M2_HOME", "%teamcity.tool.maven.DEFAULT%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    features {
        nuGetFeed {
            id = "repository-nuget-ReleaseCandidate"
            name = "ReleaseCandidate"
            description = "Staging feed for release candidate NuGet packages"
        }
    }

    subProject(ignite3_Test.Project)
    subProject(ignite3_Release.Project)
})
