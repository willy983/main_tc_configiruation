package Releases_ApacheIgniteMain.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object ApacheIgniteReleaseJava8_PrepareVote4CheckRcLicensesChecksum : BuildType({
    name = "[4] Check RC: Licenses, compile, chksum"
    description = "Run AFTER vote artifacts are uploaded to vote"

    artifactRules = "src/apache-ignite-%IGNITE_VERSION%-src/target/rat.txt => rat.txt"

    params {
        text("JIRA_VERSION", "", description = "e.g. 2.3", display = ParameterDisplay.PROMPT, allowEmpty = false)
        text("IGNITE_VERSION", "", label = "Ignite version", description = "e.g. 2.3.0", display = ParameterDisplay.PROMPT, allowEmpty = false)
        text("RC_NAME", "", label = "RC name", description = "e.g. -rc1", display = ParameterDisplay.PROMPT, allowEmpty = false)
        text("env.JAVA_HOME", "%env.JDK_ORA_8%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    vcs {
        cleanCheckout = true
    }

    steps {
        script {
            name = "Check Jira"
            enabled = false
            scriptContent = """
                curl "https://issues.apache.org/jira/rest/api/2/search?jql=project=ignite%%20AND%%20status%%20!=%%20closed%%20AND%%20fixVersion<=%JIRA_VERSION%&fields=summary" | grep '"total":0,"issues":\[\]'
                
                #curl "https://issues.apache.org/jira/rest/api/2/search?jql=project=ignite%%20AND%%20status%%20!=%%20closed%%20AND%%20fixVersion<=%JIRA_VERSION%&fields=summary"
            """.trimIndent()
        }
        script {
            name = "Download rc"
            scriptContent = """
                wget -q https://dist.apache.org/repos/dist/dev/ignite/%IGNITE_VERSION%%RC_NAME%/apache-ignite-%IGNITE_VERSION%-src.zip
                wget -q https://dist.apache.org/repos/dist/dev/ignite/%IGNITE_VERSION%%RC_NAME%/apache-ignite-%IGNITE_VERSION%-src.zip.asc
                wget -q https://dist.apache.org/repos/dist/dev/ignite/%IGNITE_VERSION%%RC_NAME%/apache-ignite-%IGNITE_VERSION%-src.zip.sha512
                wget -q https://dist.apache.org/repos/dist/dev/ignite/%IGNITE_VERSION%%RC_NAME%/apache-ignite-%IGNITE_VERSION%-bin.zip
                wget -q https://dist.apache.org/repos/dist/dev/ignite/%IGNITE_VERSION%%RC_NAME%/apache-ignite-%IGNITE_VERSION%-bin.zip.asc
                wget -q https://dist.apache.org/repos/dist/dev/ignite/%IGNITE_VERSION%%RC_NAME%/apache-ignite-%IGNITE_VERSION%-bin.zip.sha512
                
                
                if [ ! -d scr ]; then
                    mkdir src
                fi
                
                unzip -q apache-ignite-%IGNITE_VERSION%-src.zip -d src
            """.trimIndent()
        }
        maven {
            name = "Check licenses"
            goals = "validate"
            pomLocation = "src/apache-ignite-%IGNITE_VERSION%-src/pom.xml"
            runnerArgs = "-DskipTests=true -P check-licenses"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        script {
            name = "Check gpg"
            scriptContent = """
                wget https://dist.apache.org/repos/dist/release/ignite/KEYS
                
                gpg --import KEYS
                
                ascs=${'$'}(find . -name "*.asc" -type f)
                
                for asc in ${'$'}ascs
                do
                    gpg --verify ${'$'}asc
                done
            """.trimIndent()
        }
        script {
            name = "Check sha512"
            scriptContent = "sha512sum -c *.sha512"
        }
        maven {
            name = "Check source compilation"
            goals = "install"
            pomLocation = "src/apache-ignite-%IGNITE_VERSION%-src/pom.xml"
            runnerArgs = "-Pall-java,all-scala,ml -DskipTests=true"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        maven {
            name = "Check Javadoc"
            goals = "initialize"
            pomLocation = "src/apache-ignite-%IGNITE_VERSION%-src/pom.xml"
            runnerArgs = "-DskipTests=true -Pjavadoc,ml"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        maven {
            name = "Check Ignite build"
            goals = "initialize"
            pomLocation = "src/apache-ignite-%IGNITE_VERSION%-src/pom.xml"
            runnerArgs = "-DskipTests=true -Prelease,ml"
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
        maven {
            name = "Check Ignite source version"
            goals = "exec:exec"
            pomLocation = "src/apache-ignite-%IGNITE_VERSION%-src/pom.xml"
            runnerArgs = """-Dexec.executable=test "-Dexec.args=${'$'}{project.version} = %IGNITE_VERSION%" --non-recursive"""
            userSettingsSelection = "local-proxy.xml"
            localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
            jdkHome = "%env.JDK_ORA_8%"
        }
    }

    requirements {
        doesNotEqual("teamcity.agent.jvm.os.name", "Windows 10")
    }
})
