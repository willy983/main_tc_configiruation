package IgniteThinClients_Releases_PythonThinClient.buildTypes

import _Self.vcsRoots.GitHubApacheIgnitePythonThinClient
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object IgniteThinClients_Releases_PythonThinClient_ReleaseBuild : BuildType({
    name = "[1] Release Build"

    artifactRules = """
        %ARTIFACTS_DIR% => %ARTIFACTS_DIR%
        release.properties
    """.trimIndent()

    vcs {
        root(_Self.vcsRoots.GitHubApacheIgnitePythonThinClient)
    }

    steps {
        script {
            name = "Configure and set build parameters"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                # Set client version for project
                VERSION=${'$'}(python3 setup.py --version)
                
                
                set +x
                echo "##teamcity[setParameter name='VERSION' value='${'$'}{VERSION}']"
            """.trimIndent()
        }
        script {
            name = "Generate API docs"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                pip install -r requirements/docs.txt
                cd docs
                sphinx-apidoc -o . ../pygridgain
                make html
                cp -rfv generated/html ../
                cd ../
                rm -rf docs
                mv html docs
            """.trimIndent()
        }
        script {
            name = "Build binary archive"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                # Remove all files except target
                while read file
                do
                    rm -rfv ${'$'}{file}
                done < <(ls | grep -vE "(docs|examples|pygridgain|requirements|LICENSE|NOTICE|README.md|setup.py)")
                
                
                # Generate revision file
                cat <<EOF > revision.txt
                repository: ${GitHubApacheIgnitePythonThinClient.paramRefs["url"]}
                branch:     ${'$'}(git rev-parse --abbrev-ref HEAD)
                hash:       ${'$'}(git rev-parse --verify HEAD)
                EOF
                
                # Archive binary
                mkdir -pv %NAME%-%VERSION%
                cp -rfv * %NAME%-%VERSION%/
                rm -rf %NAME%-%VERSION%/%NAME%-%VERSION%
                zip -r %NAME%-%VERSION%.zip %NAME%-%VERSION%/
            """.trimIndent()
        }
        script {
            name = "Build docs archive"
            enabled = false
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                cp -rfv docs %VERSION%
                zip -r %NAME%-%VERSION%-docs.zip %VERSION%
            """.trimIndent()
        }
        script {
            name = "Build binaries"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                python3 setup.py sdist bdist_wheel
            """.trimIndent()
        }
        script {
            name = "Prepare artifacts"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                mkdir %ARTIFACTS_DIR%
                cp -rfv *.zip dist/*.tar.gz dist/*.whl %ARTIFACTS_DIR%
            """.trimIndent()
        }
        script {
            name = "Prepare properties file for Release"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                cat <<EOF > release.properties
                REVISION=${'$'}(git rev-parse --verify HEAD)
                VERSION=%VERSION%
                EOF
            """.trimIndent()
        }
        script {
            name = "Compute checksums"
            workingDir = "%ARTIFACTS_DIR%"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                file="%NAME%-%VERSION%.zip"
                base_name=${'$'}(basename ${'$'}file)
                md5sum ${'$'}{base_name} > ${'$'}{base_name}.md5
                sha512sum ${'$'}{base_name} > ${'$'}{base_name}.sha512
            """.trimIndent()
        }
    }
})
