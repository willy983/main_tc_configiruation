package ignite3_Release_Build.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object ignite3_Release_Build_JavaBinariesDocs : BuildType({
    name = "[1] Java Binaries | Docs"

    artifactRules = "%DIR_BINARIES% => %DIR_BINARIES%"

    params {
        text("DIR_BINARIES", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("DIR_MAVEN", "maven", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("PROJECT_VERSION", "${ignite3_Release_Build_Configure.depParamRefs["PROJECT_VERSION"]}", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("PROJECT_NAME", "${ignite3_Release_Build_Configure.depParamRefs["PROJECT_NAME"]}", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite3)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    steps {
        maven {
            name = "Build | Assemble binaries"
            goals = "deploy"
            pomLocation = ""
            runnerArgs = """
                -P javadoc
                -Dmaven.test.skip
                -DaltDeploymentRepository=local::default::file:%DIR_MAVEN%
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
        }
        maven {
            name = "Generate Javadoc"
            goals = "javadoc:aggregate"
            pomLocation = ""
            runnerArgs = "-P javadoc"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
        }
        script {
            name = "Prepare artifacts"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Prepare binary with Maven artifacts
                FILE_MAVEN_ARTIFACTS_BINARY="%PROJECT_NAME%-%PROJECT_VERSION%-%DIR_MAVEN%.zip"
                cd %teamcity.build.checkoutDir%/%DIR_MAVEN%
                zip -r %teamcity.build.checkoutDir%/${'$'}{FILE_MAVEN_ARTIFACTS_BINARY} *
                
                
                # Prepare binary with Javadoc
                FILE_JAVADOC_BINARY="%PROJECT_NAME%-%PROJECT_VERSION%-javadoc.zip"
                cd %teamcity.build.checkoutDir%/target/site/apidocs
                zip -r %teamcity.build.checkoutDir%/${'$'}{FILE_JAVADOC_BINARY} *
                
                
                # Gather artifacts
                cd %teamcity.build.checkoutDir%
                mkdir -pv %DIR_BINARIES%
                cp -rfv ${'$'}{FILE_MAVEN_ARTIFACTS_BINARY} \
                        ${'$'}{FILE_JAVADOC_BINARY} \
                        target/*.zip %DIR_BINARIES%/
            """.trimIndent()
        }
    }

    dependencies {
        snapshot(ignite3_Release_Build_Configure) {
            onDependencyFailure = FailureAction.CANCEL
            onDependencyCancel = FailureAction.CANCEL
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
