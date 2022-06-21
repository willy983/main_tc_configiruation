package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_ThinClientPhpOld : BuildType({
    templates(DevAi2x_ExcludeTests, DevAi2x_PreBuild, _Self.buildTypes.ThinClientStartIgnite, _Self.buildTypes.ThinClientStopIgnite, DevAi2x_PostBuild)
    name = "Thin client: PHP (old)"

    artifactRules = "work/log/** => logs.zip"

    params {
        text("ACTUAL_VERSION", "%dep.DevAi2x_Build_2.ACTUAL_VERSION%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("SKIP_BUILD_CONDITION", """! -d "modules/platforms/php"""", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "Install Composer.phar"
            id = "RUNNER_178"
            workingDir = "modules/platforms/php"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                if %system.SKIP_BUILD%; then exit 0; fi
                
                EXPECTED_SIGNATURE="${'$'}(wget -q -O - https://composer.github.io/installer.sig)"
                php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');"
                ACTUAL_SIGNATURE="${'$'}(php -r "echo hash_file('SHA384', 'composer-setup.php');")"
                
                if [ "${'$'}EXPECTED_SIGNATURE" != "${'$'}ACTUAL_SIGNATURE" ]
                then
                    >&2 echo 'ERROR: Invalid installer signature'
                    rm composer-setup.php
                    exit 1
                fi
                
                php composer-setup.php || exit 1
                php composer.phar install
            """.trimIndent()
        }
        script {
            name = "Run PHP tests"
            id = "RUNNER_186"
            workingDir = "modules/platforms/php"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                if %system.SKIP_BUILD%; then exit 0; fi
                
                
                # Run tests
                vendor/phpunit/phpunit/phpunit tests --teamcity
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_159", "RUNNER_264", "RUNNER_287", "RUNNER_178", "RUNNER_3", "RUNNER_186", "RUNNER_12", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_35")
    }
})
