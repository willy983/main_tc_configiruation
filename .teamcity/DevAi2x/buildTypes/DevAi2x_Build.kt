package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_Build : BuildType({
    name = "> Build"
    description = "Build and prepare project for testing"

    artifactRules = """
        ignite.zip
        repository.zip
        **/checkstyle-result.xml => checkstyle.zip
        **/target/rat.txt => rat.zip
    """.trimIndent()

    params {
        text("FLAG_RUN_SANITY_CHECKS", "true", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("JVM_EXTRA_ARGS", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    steps {
        script {
            name = "Clean up local Maven repository"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                rm -rfv ~/.m2/repository/org/apache/ignite
            """.trimIndent()
        }
        maven {
            name = "Build Apache Ignite"
            goals = "install"
            pomLocation = ""
            runnerArgs = """
                -U
                -P %MAVEN_PROFILES%,check-test-suites
                -DskipTests
                -Dmaven.javadoc.skip=true
                -Dmaven.source.skip=true
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        maven {
            name = "Check Code Style"

            conditions {
                equals("FLAG_RUN_SANITY_CHECKS", "true")
            }
            goals = "checkstyle:checkstyle"
            pomLocation = ""
            runnerArgs = "-P %MAVEN_PROFILES%,checkstyle"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        maven {
            name = "Check Javadoc"

            conditions {
                equals("FLAG_RUN_SANITY_CHECKS", "true")
            }
            goals = "initialize"
            pomLocation = ""
            runnerArgs = "-P javadoc"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        maven {
            name = "Check License Headers"

            conditions {
                equals("FLAG_RUN_SANITY_CHECKS", "true")
            }
            goals = "validate"
            pomLocation = ""
            runnerArgs = "-P check-licenses"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        maven {
            name = "Check Missing Tests (prepare modules)"

            conditions {
                equals("FLAG_RUN_SANITY_CHECKS", "true")
            }
            goals = "surefire:test"
            pomLocation = ""
            runnerArgs = "-P %MAVEN_PROFILES%,check-test-suites"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        maven {
            name = "Check Missing Tests"

            conditions {
                equals("FLAG_RUN_SANITY_CHECKS", "true")
            }
            goals = "test"
            pomLocation = ""
            runnerArgs = """
                -N
                -P %MAVEN_PROFILES%,check-test-suites
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        script {
            name = "Prepare ignite-tools for rebuilding"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                rm -rfv modules/tools/target
            """.trimIndent()
        }
        maven {
            name = "Rebuild ignite-tools (to restore default surefire provider)"
            goals = "install"
            pomLocation = ""
            runnerArgs = """
                -pl :ignite-tools -am
                -DskipTests
                -Dmaven.javadoc.skip
                -Dmaven.source.skip
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        script {
            name = "Prepare built artifacts"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Prepare archive with 'target' directories
                zip -r ignite target modules/{*,*/*}/target -x '*.jar'
                zip -ur ignite modules/{*,*/*}/target -i */aspectjweaver-*.jar \
                                                      -i */cache-api-*.jar \
                                                      -i */h2-*.jar
                
                
                # Prepare archive with installed artifacts
                mkdir -pv repository/org/apache/ignite
                cp -rfv ~/.m2/repository/org/apache/ignite/* repository/org/apache/ignite/
                zip -r repository repository
            """.trimIndent()
        }
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
        startsWith("teamcity.agent.name", "aitc")
    }
})
