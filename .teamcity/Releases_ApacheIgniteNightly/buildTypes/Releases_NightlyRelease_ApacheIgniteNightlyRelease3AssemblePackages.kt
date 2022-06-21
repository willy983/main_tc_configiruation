package Releases_ApacheIgniteNightly.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object Releases_NightlyRelease_ApacheIgniteNightlyRelease3AssemblePackages : BuildType({
    name = "[APACHE IGNITE NIGHTLY RELEASE] #3 :: Assemble Linux Packages"

    artifactRules = """
        packaging/*.rpm
        packaging/*.deb
    """.trimIndent()

    params {
        text("IGNITE_VERSION", "${Releases_NightlyRelease_ApacheIgniteNightlyReleasePrepare.depParamRefs["IGNITE_VERSION"]}", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)
    }

    steps {
        script {
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                #
                # Build packages
                #
                # Workaround for arch-dependant files in package
                grep -q '_binaries_in_noarch_packages_terminate_build' packaging/rpm/apache-ignite.spec \
                	|| sed '1h;1!H;${'$'}!d;x;s/.*%define[^\n]*/&\n%define _binaries_in_noarch_packages_terminate_build 0/' -i packaging/rpm/apache-ignite.spec
                packaging/package.sh --rpm --deb
            """.trimIndent()
        }
    }

    dependencies {
        dependency(Releases_NightlyRelease_ApacheIgniteNightlyReleaseAssembleBinaries) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                artifactRules = "apache-ignite-%IGNITE_VERSION%-bin.zip => packaging"
            }
        }
        dependency(Releases_NightlyRelease_ApacheIgniteNightlyReleasePrepare) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                artifactRules = "apache-ignite-%IGNITE_VERSION%-src.zip!** => ."
            }
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
