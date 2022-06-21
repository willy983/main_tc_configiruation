package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object IgniteTests24Java8_BuildApacheIgnite : BuildType({
    name = "[Build]"
    description = "Build and prepare project for testing"

    artifactRules = """
        **/* => ignite.zip
        ./**/target/checkstyle-result.xml => checkstyle-result.zip
    """.trimIndent()

    params {
        param("MAVEN_MODULES_STRING", "")
        text("ACTUAL_VERSION", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("env.JAVA_HOME", "%env.JDK_ORA_8%")
        text("JVM_EXTRA_ARGS", "", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        checkoutMode = CheckoutMode.ON_SERVER
        cleanCheckout = true
    }

    steps {
        script {
            name = "Setup additional arguments"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                echo "##teamcity[setParameter name='env.JAVA_HOME' value='%env.JDK_ORA_8%']"
                
                # Java 9
                JAVA_VERSION=${'$'}(echo %env.JAVA_HOME% | sed -r 's;.*((java|jdk-.*)-(8|9|10|11)).*;\3;')
                echo ${'$'}{JAVA_VERSION}
                if [ "${'$'}{JAVA_VERSION}" == "8" ]
                then
                    :
                elif [ "${'$'}{JAVA_VERSION}" == "9" -o "${'$'}{JAVA_VERSION}" == "10" ]
                then
                    echo "##teamcity[setParameter name='JVM_EXTRA_ARGS' value='--illegal-access=permit --add-exports=java.base/jdk.internal.misc=ALL-UNNAMED --add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-exports=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED --add-exports=jdk.internal.jvmstat/sun.jvmstat.monitor=ALL-UNNAMED --add-exports=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED --add-modules=java.xml.bind']"
                #    echo "##teamcity[setParameter name='MAVEN_MODULES_STRING' value='-pl -:ignite-scalar_2.10']"
                elif [ "${'$'}{JAVA_VERSION}" == "11" ]
                then
                    echo "##teamcity[setParameter name='JVM_EXTRA_ARGS' value='--illegal-access=permit --add-exports=java.base/jdk.internal.misc=ALL-UNNAMED --add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-exports=java.management/com.sun.jmx.mbeanserver=ALL-UNNAMED --add-exports=jdk.internal.jvmstat/sun.jvmstat.monitor=ALL-UNNAMED --add-exports=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED -Djdk.tls.client.protocols=TLSv1.2']"
                else
                    echo "Wrong JAVA detected, exiting."
                    exit 1
                fi
                
                # Actual version
                actualVersion=${'$'}(cat pom.xml | \
                                grep -E "<version>([0-9]+\.)+[0-9]+(-SNAPSHOT)?</version>" | \
                                head -1 | \
                                sed -r 's|.*>([0-9]+\.[0-9]+).*<.*|\1|')
                echo "##teamcity[setParameter name='ACTUAL_VERSION' value='${'$'}{actualVersion}']"
            """.trimIndent()
        }
        script {
            name = "Cleanup local maven repository"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                rm -rfv ~/.m2/repository/org/apache/ignite
            """.trimIndent()
        }
        maven {
            name = "Build Apache Ignite"
            goals = "install"
            runnerArgs = """
                -U
                -Pall-java,all-scala,scala,licenses,lgpl,examples,checkstyle
                -DskipTests
                -Dmaven.javadoc.skip=true
                %MAVEN_MODULES_STRING%
            """.trimIndent()
            mavenVersion = custom {
                path = "%teamcity.tool.maven.3.6.0%"
            }
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        script {
            name = "Prepare built artifacts"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                mkdir -pv repository
                cp -rfv ~/.m2/repository/org/apache/ignite/* repository/
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
