package Releases_ApacheIgniteNightly.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object Releases_NightlyRelease_ApacheIgniteNightlyReleasePrepare : BuildType({
    name = "[APACHE IGNITE NIGHTLY RELEASE] #0 :: Prepare"

    artifactRules = "apache-ignite-%IGNITE_VERSION%-src.zip"

    params {
        param("env.JAVA_HOME", "%env.JDK_ORA_8%")
        text("IGNITE_VERSION", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        cleanCheckout = true
    }

    steps {
        script {
            name = "Generate nightly release version"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                igniteVersion=${'$'}(cat parent/pom.xml | \
                              grep -E -m 1 '<revision>[0-9]+.[0-9]+.[0-9]+(-SNAPSHOT)?</revision>'| \
                              sed -r 's|.*>(.*)<.*|\1|' | sed -r 's|(.*)-SNAPSHOT|\1|').${'$'}(date +%%Y%%m%%d -d "+3 hours")
                echo "##teamcity[setParameter name='IGNITE_VERSION' value='${'$'}{igniteVersion}']"
            """.trimIndent()
        }
        script {
            scriptContent = "ls -lar packaging"
        }
        maven {
            name = "Change .NET & C++ versions"
            goals = "validate"
            pomLocation = ""
            runnerArgs = """
                -P update-versions
                -Dnew.ignite.version=%IGNITE_VERSION%
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
        }
        maven {
            name = "[OLD] Change MAVEN version"
            enabled = false
            goals = "versions:set"
            pomLocation = "parent/pom.xml"
            runnerArgs = """
                -N
                -DnewVersion=%IGNITE_VERSION%
                -DgenerateBackupPoms=false
                -DgroupId=*
                -DartifactId=*
                -DoldVersion=*
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
        }
        script {
            name = "[NEW] Change MAVEN version"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                sed -r 's|(.*<revision>).*(</revision>)|\1%IGNITE_VERSION%\2|' -i parent/pom.xml
            """.trimIndent()
        }
        script {
            name = "Change packages version"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                sed -i -r -e '0,/Version:/s/(.*)[0-9]+\.[0-9]+.[0-9]+/\1%IGNITE_VERSION%/' \
                          -e '0,/^\*/s|(.*-\ ).*(-[0-9]+.*)|\1%IGNITE_VERSION%\2|' \
                    packaging/rpm/apache-ignite.spec
                sed -i -r '0,/apache-ignite/s|(.*\().*(-[0-9]+\).*)|\1%IGNITE_VERSION%\2|' \
                    packaging/deb/changelog
            """.trimIndent()
        }
        script {
            name = "[HACK] Override version pattern"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                sed -i -r -e 's|\(\\\\d\+\)\(|\(\\\\d\+\)\(\\\\.\\\\d\+\)\?\(|' \
                          -e 's|match.group\(9\)|match.group(10)|' \
                          -e 's|match.group\(8\)|match.group(9)|'  \
                          -e 's|match.group\(7\)|match.group(8)|'  \
                          -e 's|match.group\(4\)|match.group(5)|'  \
                    modules/core/src/main/java/org/apache/ignite/lang/IgniteProductVersion.java
            """.trimIndent()
        }
        script {
            name = "Prepare sources artifact"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                git config user.email "apache.ignite.tc@gmail.com"
                git config user.name "Apache Ignite TeamCity"
                git archive ${'$'}(git stash create) --format=zip -o apache-ignite-%IGNITE_VERSION%-src.zip
            """.trimIndent()
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
