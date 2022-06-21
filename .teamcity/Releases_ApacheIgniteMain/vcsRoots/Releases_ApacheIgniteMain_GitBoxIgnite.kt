package Releases_ApacheIgniteMain.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object Releases_ApacheIgniteMain_GitBoxIgnite : GitVcsRoot({
    name = "GitBox [ignite]"
    pollInterval = 180
    url = "https://gitbox.apache.org/repos/asf/ignite.git"
    branch = "master"
    branchSpec = """
        +:refs/heads/(ignite-*)
        +:refs/(pull/*/head)
    """.trimIndent()
    userNameStyle = GitVcsRoot.UserNameStyle.FULL
    checkoutPolicy = GitVcsRoot.AgentCheckoutPolicy.NO_MIRRORS
})
