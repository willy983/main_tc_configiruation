package Releases_ApacheIgniteMain.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object Releases_ApacheIgniteMain_ReleaseBuild_1 : BuildType({
    id("Releases_ApacheIgniteMain_ReleaseBuild")
    name = "[1] Release Build"
    description = "Assemble Apache Ignite release artifacts"

    artifactRules = "release => release-%IGNITE_VERSION%-%RC_NAME%.zip"

    params {
        text("reverse.dep.*.IGNITE_VERSION", "", label = "Ignite version", display = ParameterDisplay.PROMPT,
              regex = "[0-9]+.[0-9]+.[0-9]+", validationMessage = "Version format: '<major>.<minor>.<fix>'")
        text("JVM_ARGS", """
            -Xmx1g
            -XX:MaxPermSize=512m
        """.trimIndent(), display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("reverse.dep.*.RC_NAME", "", label = "Release candidate suffix", display = ParameterDisplay.PROMPT,
              regex = "rc[0-9]+", validationMessage = "Release candidate suffix format: 'rc<index>'")
        text("IGNITE_ROOT", "ignite", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("IGNITE_RELEASE_ROOT", "ignite-release", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("IGNITE_VERSION", "%reverse.dep.*.IGNITE_VERSION%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("RC_NAME", "%reverse.dep.*.RC_NAME%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.JAVA_HOME", "%env.JDK_ORA_8%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgniteRelease, "+:. => %IGNITE_RELEASE_ROOT%")
        root(_Self.vcsRoots.GitHubApacheIgnite, "+:. => %IGNITE_ROOT%")

        cleanCheckout = true
    }

    steps {
        script {
            name = "Apply code changes from patch"
            workingDir = "%IGNITE_ROOT%"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                if [ -s ../vote.patch ]
                then
                	git apply ../vote.patch
                else
                	echo "Empty ../vote.patch file, nothing to apply"
                fi
            """.trimIndent()
        }
        script {
            name = "Copy release scripts"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                mkdir release
                cp -rfv %IGNITE_RELEASE_ROOT%/scripts/* release/
            """.trimIndent()
        }
        script {
            name = "Git setup"
            workingDir = "%IGNITE_ROOT%"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                # Ignore chmod
                git config core.filemode false
                
                # Set user / mail
                git config --global user.name "Ignite Teamcity"
                git config --global user.email ignite@apache.org
                
                # Fix URL in .git/config
                sed -i -r -e 's|^\[url.*||' \
                          -e 's|.*nsteadOf.*||' \
                          -e '/^${'$'}/d' \
                    .git/config
            """.trimIndent()
        }
        script {
            name = "Archive sources"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                mkdir -pv release/svn/vote
                cd %IGNITE_ROOT%
                git archive HEAD \
                    --prefix=apache-ignite-%reverse.dep.*.IGNITE_VERSION%-src/ \
                    --format=zip -o ../release/svn/vote/apache-ignite-%reverse.dep.*.IGNITE_VERSION%-src.zip \
            """.trimIndent()
        }
        script {
            name = "Save Git repo for release tagging"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                cp -rf %IGNITE_ROOT% release/git/
            """.trimIndent()
        }
        script {
            name = "Create release properties file"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                echo "rc_name=\"-%RC_NAME%\"" > release/release.properties
                echo "ignite_version=\"%IGNITE_VERSION%\"" >> release/release.properties
            """.trimIndent()
        }
        maven {
            name = "[TEMP] Install tools (to be removed after IGNITE-6727)"
            goals = "install"
            pomLocation = ""
            runnerArgs = "-pl modules/tools -am"
            workingDir = "%IGNITE_ROOT%"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
        }
        maven {
            name = "Build Java"
            goals = "deploy"
            pomLocation = "%IGNITE_ROOT%/pom.xml"
            runnerArgs = """
                -Pall-java,all-scala,licenses
                -DclientDocs
                -DskipTests
                -DaltDeploymentRepository=local::default::file:release/maven
            """.trimIndent()
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jvmArgs = "%JVM_ARGS%"
        }
        powerShell {
            name = "Build .NET"
            scriptMode = file {
                path = "%IGNITE_ROOT%/modules/platforms/dotnet/build.ps1"
            }
            param("jetbrains_powershell_scriptArguments", """
                -skipJava
                -skipExamples
            """.trimIndent())
        }
        maven {
            name = "Generate javadoc"
            goals = "initialize"
            pomLocation = ""
            runnerArgs = "-Pjavadoc"
            workingDir = "%IGNITE_ROOT%"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jvmArgs = "%JVM_ARGS%"
        }
        script {
            name = "Copy .NET docs"
            enabled = false
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                cp -rf dotnetdoc %IGNITE_ROOT%/modules/clients/target
            """.trimIndent()
        }
        maven {
            name = "Assembly Apache Ignite Full Binaries"
            goals = "initialize"
            pomLocation = ""
            runnerArgs = """
                -Prelease,numa-allocator,all-scala
                -Dignite.edition=apache-ignite
            """.trimIndent()
            workingDir = "%IGNITE_ROOT%"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jvmArgs = "%JVM_ARGS%"
        }
        maven {
            name = "Assembly Apache Ignite Slim Binaries"
            goals = "initialize"
            pomLocation = ""
            runnerArgs = """
                -Prelease,numa-allocator,all-scala
                -Dignite.edition=apache-ignite-slim
            """.trimIndent()
            workingDir = "%IGNITE_ROOT%"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jvmArgs = "%JVM_ARGS%"
        }
        script {
            name = "Assemble RPM/DEB packages"
            workingDir = "%IGNITE_ROOT%"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                cp -rfv target/bin/apache-ignite-%IGNITE_VERSION%-bin.zip packaging/
                # Workaround for arch-dependant files in package
                grep -q '_binaries_in_noarch_packages_terminate_build' packaging/rpm/apache-ignite.spec \
                	|| sed '1h;1!H;${'$'}!d;x;s/.*%define[^\n]*/&\n%define _binaries_in_noarch_packages_terminate_build 0/' -i packaging/rpm/apache-ignite.spec
                bash packaging/package.sh --rpm
                bash packaging/package.sh --deb
            """.trimIndent()
        }
        script {
            name = "Prepare binaries and checkums"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                PACKAGE_DIR="release/packages"
                NUGET_DIR="apache-ignite-%IGNITE_VERSION%-nuget"
                
                
                # Prepare files
                mkdir -pv "${'$'}{NUGET_DIR}"
                mv -fv %IGNITE_ROOT%/modules/platforms/dotnet/nupkg/*.nupkg "${'$'}{NUGET_DIR}"
                zip -r ${'$'}{NUGET_DIR}.zip ${'$'}{NUGET_DIR}/
                
                # Copy files
                mkdir -p ${'$'}{PACKAGE_DIR}/pkg
                mv -fv %IGNITE_ROOT%/target/bin/*.* ${'$'}{NUGET_DIR}.zip release/svn/vote
                mv -fv %IGNITE_ROOT%/packaging/{*.rpm,*.deb} ${'$'}{PACKAGE_DIR}/pkg
                mv -fv %IGNITE_ROOT%/packaging/rpm ${'$'}{PACKAGE_DIR}
                mv -fv %IGNITE_ROOT%/packaging/deb ${'$'}{PACKAGE_DIR}
                
                
                # Calculate checkksums
                cd release/svn/vote
                list=${'$'}(find . -type f -name "*.zip")
                for file in ${'$'}list
                do
                    base_name=${'$'}(basename ${'$'}file)
                    sha512sum ${'$'}base_name > ${'$'}base_name.sha512
                done
            """.trimIndent()
        }
    }

    dependencies {
        dependency(Releases_ApacheIgniteMain_ReleaseBuild.buildTypes.Releases_ApacheIgniteMain_ReleaseBuild_PrepareBuildOdbc) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                artifactRules = """
                    ignite.odbc.installers.zip!** => %IGNITE_ROOT%/modules/platforms/cpp/bin/odbc
                    vote.patch
                """.trimIndent()
            }
        }
    }

    requirements {
        contains("teamcity.agent.jvm.os.name", "Linux")
    }
})
