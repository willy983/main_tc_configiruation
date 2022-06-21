package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object DevAi2x_RunMl : BuildType({
    name = "-> Run :: ML"

    type = BuildTypeSettings.Type.COMPOSITE

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        showDependenciesChanges = true
    }

    dependencies {
        snapshot(DevAi2x_Examples) {
        }
        snapshot(DevAi2x_Ml) {
        }
    }
})
