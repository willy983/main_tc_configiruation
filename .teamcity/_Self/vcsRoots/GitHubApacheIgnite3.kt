package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object GitHubApacheIgnite3 : GitVcsRoot({
    name = "GitHub [apache/ignite-3]"
    url = "https://github.com/apache/ignite-3.git"
    branch = "main"
    branchSpec = """
        +:refs/heads/(main)
        +:refs/heads/(ignite-*)
        +:refs/(pull/*)/head
    """.trimIndent()
    userNameStyle = GitVcsRoot.UserNameStyle.FULL
    param("useAlternates", "true")
})
