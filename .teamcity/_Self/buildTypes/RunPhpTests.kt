package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object RunPhpTests : Template({
    name = "Run PHP tests"
    description = "Installs Composer and runs main tests"

    params {
        text("VCS_ROOT_PHP", "ignite-php-thin-client", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "Install Composer.phar"
            id = "RUNNER_87"
            workingDir = "%VCS_ROOT_PHP%"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
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
            id = "RUNNER_88"
            workingDir = "%VCS_ROOT_PHP%"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Run tests
                vendor/phpunit/phpunit/phpunit tests --teamcity
            """.trimIndent()
        }
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_17")
    }
})
