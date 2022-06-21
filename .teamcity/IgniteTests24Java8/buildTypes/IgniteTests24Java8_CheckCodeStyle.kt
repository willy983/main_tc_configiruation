package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object IgniteTests24Java8_CheckCodeStyle : BuildType({
    name = "[Check Code Style]"

    artifactRules = """
        work/log => logs.zip
        ./**/target/checkstyle-result.xml => checkstyle-result.zip
    """.trimIndent()

    params {
        text("env.JAVA_HOME", "%env.JDK_ORA_8%", allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    steps {
        script {
            name = "Cleanup .m2/repository"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                rm -rf ~/.m2/repository/org/apache/ignite
            """.trimIndent()
        }
        maven {
            name = "Check Code Style by Maven plugin"
            goals = "package"
            pomLocation = ""
            runnerArgs = """
                -Pcheckstyle,all-java,all-scala,all-other,compatibility,lgpl,yardstick,benchmarks,examples,web-console
                -DskipTests
                -Dmaven.javadoc.skip=true
                -Dmaven.source.skip=true
                -pl -:ignite-scalar_2.10,-:ignite-scalar,-:ignite-visor-console,-:ignite-visor-console_2.10
            """.trimIndent()
            mavenVersion = custom {
                path = "%teamcity.tool.maven.3.6.0%"
            }
            userSettingsSelection = "local-proxy.xml"
            userSettingsPath = "settings.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
