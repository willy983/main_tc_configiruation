package Releases_ApacheIgniteNightly.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object Releases_NightlyRelease_ApacheIgniteNightlyReleaseAssembleBinaries : BuildType({
    name = "[APACHE IGNITE NIGHTLY RELEASE] #2 :: Assemble Binaries"

    artifactRules = """
        target/bin/*.zip => .
        modules/platforms/dotnet/nupkg/*.nupkg => apache-ignite-%IGNITE_VERSION%-nuget-staging.zip
        staging => apache-ignite-%IGNITE_VERSION%-maven-staging.zip
    """.trimIndent()

    params {
        text("env.JAVA_HOME", "%env.JDK_ORA_8%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        password("MYGET_PASSWORD", "credentialsJSON:8b7a8bf7-632b-499a-943b-41720c9ef320", display = ParameterDisplay.HIDDEN)
        text("IGNITE_VERSION", "${Releases_NightlyRelease_ApacheIgniteNightlyReleasePrepare.depParamRefs["IGNITE_VERSION"]}", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnite)

        cleanCheckout = true
    }

    steps {
        script {
            name = "Prepare custom settings.xml"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                cat <<EOF > settings.xml
                <?xml version="1.0" encoding="UTF-8"?>
                <settings>
                  <servers>
                    <server>
                      <id>myget</id>
                      <username>apache-ignite</username>
                      <password>\${'$'}{myget.password}</password>
                    </server>
                    <server>
                      <id>local-proxy</id>
                    </server>
                  </servers>
                  <profiles>
                    <profile>
                      <id>artifactory</id>
                      <repositories>
                        <repository>
                          <snapshots>
                            <enabled>false</enabled>
                          </snapshots>
                          <id>local-proxy</id>
                          <name>local-proxy</name>
                          <url>http://172.25.4.107/artifactory/local-proxy</url>
                        </repository>
                      </repositories>
                      <pluginRepositories>
                        <pluginRepository>
                          <snapshots>
                            <enabled>false</enabled>
                          </snapshots>
                          <id>local-proxy</id>
                          <name>local-proxy</name>
                          <url>http://172.25.4.107/artifactory/local-proxy</url>
                        </pluginRepository>
                      </pluginRepositories>
                    </profile>
                  </profiles>
                  <activeProfiles>
                    <activeProfile>artifactory</activeProfile>
                  </activeProfiles>
                </settings>
                EOF
            """.trimIndent()
        }
        maven {
            name = "Build"
            enabled = false
            goals = "package"
            pomLocation = ""
            runnerArgs = """
                -pl -:ignite-osgi-karaf -am
                -Pall-java,all-scala,licenses
                -DclientDocs
                -DskipTests
            """.trimIndent()
            userSettingsSelection = "userSettingsSelection:byPath"
            userSettingsPath = "settings.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jvmArgs = """
                -Xmx1g
                -XX:MaxPermSize=512m
            """.trimIndent()
        }
        maven {
            name = "Build Java"
            goals = "deploy"
            pomLocation = ""
            runnerArgs = """
                -Pall-java,all-scala,licenses
                -DclientDocs
                -DskipTests
                -DaltDeploymentRepository=local::default::file:staging
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jvmArgs = """
                -Xmx1g
                -XX:MaxPermSize=512m
            """.trimIndent()
        }
        powerShell {
            name = "Build .NET"
            scriptMode = file {
                path = "modules/platforms/dotnet/build.ps1"
            }
            param("jetbrains_powershell_scriptArguments", """
                -skipJava
                -skipExamples
                -versionSuffix "-nightly"
            """.trimIndent())
        }
        maven {
            name = "Generate Javadoc"
            goals = "validate"
            runnerArgs = "-Pjavadoc"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jvmArgs = "-Xmx1g -XX:MaxPermSize=512m"
        }
        maven {
            name = "Assembly Apache Ignite"
            goals = "initialize"
            pomLocation = ""
            runnerArgs = """
                -Prelease,numa-allocator,all-scala
                -Dignite.edition=apache-ignite
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jvmArgs = "-Xmx1g -XX:MaxPermSize=512m"
        }
        maven {
            name = "Assembly Apache Ignite Slim"
            goals = "initialize"
            pomLocation = ""
            runnerArgs = """
                -Prelease,numa-allocator,all-scala
                -Dignite.edition=apache-ignite-slim
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jvmArgs = "-Xmx1g -XX:MaxPermSize=512m"
        }
    }

    dependencies {
        dependency(Releases_NightlyRelease_ApacheIgniteNightlyReleaseBuildNetCpp) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                cleanDestination = true
                artifactRules = "ignite.odbc.installers.zip!** => modules/platforms/cpp/bin/odbc"
            }
        }
        dependency(Releases_NightlyRelease_ApacheIgniteNightlyReleasePrepare) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                cleanDestination = true
                artifactRules = "apache-ignite-%IGNITE_VERSION%-src.zip!** => ."
            }
        }
    }

    requirements {
        contains("teamcity.agent.jvm.os.name", "Linux")
    }
})
