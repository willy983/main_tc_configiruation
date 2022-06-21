package ignite3.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object ignite3_ApacheIgniteBuildDependencyWindows : Template({
    name = "Apache Ignite Build Dependency (Windows)"

    params {
        text("PATH__M2_REPOSITORY", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
        showDependenciesChanges = true
    }

    steps {
        script {
            name = "Get local Maven repository dir"
            id = "RUNNER_102"
            scriptContent = """
                @echo on
                
                
                FOR /f "tokens=* delims=" %%%%F in ('"C:\BuildAgent\tools\maven3_6\bin\mvn.cmd help:evaluate -Dexpression=settings.localRepository -q -DforceStdout"') do @set "PATH__M2_REPOSITORY=%%%%F"
                echo %%PATH__M2_REPOSITORY%%
                
                
                @echo off
                echo ##teamcity[setParameter name='PATH__M2_REPOSITORY' value='%%PATH__M2_REPOSITORY%%']
            """.trimIndent()
        }
        script {
            name = "Import Apache Ignite artifacts"
            id = "RUNNER_97"
            scriptContent = """
                @echo on
                
                
                del /s /f /q %PATH__M2_REPOSITORY%\org\apache\ignite
                xcopy repository\* %PATH__M2_REPOSITORY%\ /s
                exit /b
            """.trimIndent()
        }
    }

    failureConditions {
        executionTimeoutMin = 120
    }

    dependencies {
        dependency(ignite3_BuildApacheIgnite) {
            snapshot {
            }

            artifacts {
                id = "ARTIFACT_DEPENDENCY_14"
                artifactRules = """
                    ignite.zip!** => .
                    repository.zip!** => .
                """.trimIndent()
            }
        }
    }

    requirements {
        startsWith("teamcity.agent.jvm.os.name", "Windows", "RQ_18")
    }
})
