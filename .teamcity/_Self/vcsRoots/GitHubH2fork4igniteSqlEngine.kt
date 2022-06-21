package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object GitHubH2fork4igniteSqlEngine : GitVcsRoot({
    name = "GitHub [h2fork4ignite/sql-engine]"
    url = "https://github.com/h2fork4ignite/sql-engine.git"
    branch = "refs/heads/master"
    branchSpec = """
        +:refs/heads/(*)
        +:refs/(pull/*/head)
    """.trimIndent()
    userNameStyle = GitVcsRoot.UserNameStyle.FULL
    param("useAlternates", "true")
})
