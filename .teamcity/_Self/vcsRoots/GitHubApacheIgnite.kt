package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object GitHubApacheIgnite : GitVcsRoot({
    name = "GitHub [apache/ignite]"
    url = "https://github.com/apache/ignite.git"
    branch = "refs/heads/master"
    branchSpec = """
        +:refs/heads/(ignite-*)
        +:refs/(pull/*/head)
        +:refs/heads/(sql-calcite)
    """.trimIndent()
    userNameStyle = GitVcsRoot.UserNameStyle.FULL
    param("useAlternates", "true")
})
