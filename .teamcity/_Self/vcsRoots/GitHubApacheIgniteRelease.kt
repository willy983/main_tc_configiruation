package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object GitHubApacheIgniteRelease : GitVcsRoot({
    name = "GitHub [apache/ignite-release]"
    url = "https://github.com/apache/ignite-release.git"
    branch = "refs/heads/master"
    branchSpec = "+:refs/heads/(*)"
    useTagsAsBranches = true
    userNameStyle = GitVcsRoot.UserNameStyle.FULL
    param("useAlternates", "true")
})
