package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object GitHubApacheIgnitePhpThinClient : GitVcsRoot({
    name = "GitHub [apache/ignite-php-thin-client]"
    url = "https://github.com/apache/ignite-php-thin-client.git"
    branch = "refs/heads/master"
    branchSpec = """
        +:refs/heads/(ignite-*)
        +:refs/(pull/*/head)
    """.trimIndent()
    userNameStyle = GitVcsRoot.UserNameStyle.FULL
    param("useAlternates", "true")
})
