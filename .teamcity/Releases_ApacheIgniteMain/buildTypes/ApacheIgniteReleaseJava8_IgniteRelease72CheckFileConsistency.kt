package Releases_ApacheIgniteMain.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object ApacheIgniteReleaseJava8_IgniteRelease72CheckFileConsistency : BuildType({
    name = "[2] Compare w/ Previous Release"

    artifactRules = """
        result.log => results
        cur.log => results
        prev.log => results
    """.trimIndent()

    params {
        text("PREV_IGNITE_VERSION", "", label = "Previous Ignite version", description = "X.X.X", display = ParameterDisplay.PROMPT, allowEmpty = true)
        text("IGNITE_VERSION", "", label = "Current Ignite version", description = "X.X.X", display = ParameterDisplay.PROMPT, allowEmpty = true)
    }

    vcs {
        checkoutMode = CheckoutMode.ON_AGENT
        cleanCheckout = true
    }

    steps {
        script {
            name = "Download previous release"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                mkdir -pv raw/prev
                cd raw/prev
                
                wget http://apache-mirror.rbc.ru/pub/apache/ignite/%PREV_IGNITE_VERSION%/apache-ignite-%PREV_IGNITE_VERSION%-bin.zip || exit 1
                #wget http://apache-mirror.rbc.ru/pub/apache/ignite/%PREV_IGNITE_VERSION%/apache-ignite-hadoop-%PREV_IGNITE_VERSION%-bin.zip
                wget http://apache-mirror.rbc.ru/pub/apache/ignite/%PREV_IGNITE_VERSION%/apache-ignite-%PREV_IGNITE_VERSION%-src.zip || exit 1
            """.trimIndent()
        }
        script {
            name = "Compare"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                
                mkdir -pv compare/prev
                mkdir -pv compare/cur
                
                zip_files=${'$'}(find ./raw/prev -type f -name "*.zip")
                for zip_file in ${'$'}zip_files
                do
                    unzip -q ${'$'}zip_file -d ./compare/prev
                done
                
                zip_files=${'$'}(find ./raw/cur -type f -name "*.zip")
                for zip_file in ${'$'}zip_files
                do
                    unzip -q ${'$'}zip_file -d ./compare/cur
                done
                
                find compare/prev | sort > prev.log  
                find compare/cur | sort > cur.log
                
                sed -i "s/compare\/prev/ /g" prev.log  
                sed -i "s/compare\/cur/ /g" cur.log  
                
                sed -i "s/[0-9]\+[.][0-9]\+[.][0-9]\+/x.x.x/g" prev.log  
                sed -i "s/[0-9]\+[.][0-9]\+[.][0-9]\+/x.x.x/g" cur.log  
                
                sed -i "s/-fabric//g" prev.log   
                
                sort -o prev.log prev.log
                sort -o cur.log cur.log
                
                diff -U0 prev.log cur.log > result.log
                
                
                # Cleanup
                sed -i "s/[@][@].*[@][@]//g" result.log
                sed -i '/^${'$'}/d' result.log
            """.trimIndent()
        }
    }

    dependencies {
        artifacts(Releases_ApacheIgniteMain_ReleaseBuild_1) {
            buildRule = lastSuccessful("ignite-%IGNITE_VERSION%")
            artifactRules = "release*.zip!svn/vote** => raw/cur"
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux")
    }
})
