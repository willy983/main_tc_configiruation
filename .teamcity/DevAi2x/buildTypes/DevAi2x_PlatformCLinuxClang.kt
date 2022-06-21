package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_PlatformCLinuxClang : BuildType({
    templates(DevAi2x_RunTestSuitesJava, DevAi2x_C)
    name = "Platform C++ (Linux Clang)"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        ./dev-tools/IGNITE-*-*.patch => patch
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
    """.trimIndent()

    params {
        select("CLANG_VERSION", "3.9", label = "CLang Version",
                options = listOf("3.9"))
        text("env.PATH", "/usr/lib/llvm-%CLANG_VERSION%/bin:%env.PATH%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "Build C++ (Linux)"
            id = "RUNNER_18"
            workingDir = "modules/platforms/cpp"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                rm -rfv %env.CPP_STAGING%
                mkdir -pv %env.CPP_STAGING%
                
                libtoolize --copy
                aclocal
                autoheader
                automake --add-missing --copy
                autoreconf
                ./configure --enable-odbc --enable-tests --prefix=%env.CPP_STAGING% CXX=clang++ CC=clang CPPFLAGS=-I%env.CPP_STAGING%/include LDFLAGS=-L%env.CPP_STAGING%/lib
                make -j4 || exit 1
                make install || exit 1
            """.trimIndent()
        }
        script {
            name = "Build C++ Examples (Linux)"
            id = "RUNNER_20"
            workingDir = "modules/platforms/cpp/examples"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                libtoolize --copy
                aclocal
                autoheader
                automake --add-missing --copy
                autoreconf
                ./configure --prefix=%env.CPP_STAGING% CXX=clang++ CC=clang CPPFLAGS=-I%env.CPP_STAGING%/include LDFLAGS=-L%env.CPP_STAGING%/lib
                make -j4 || exit 1
                make install || exit 1
            """.trimIndent()
        }
        script {
            name = "Run Core tests (Linux)"
            id = "RUNNER_23"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "modules/platforms/cpp/core-test"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                ./ignite-tests || echo "Some tests failed"
            """.trimIndent()
        }
        script {
            name = "Run ODBC tests (Linux)"
            id = "RUNNER_24"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "modules/platforms/cpp/odbc-test"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                ./ignite-odbc-tests || echo "Some tests failed"
            """.trimIndent()
        }
        script {
            name = "Run Thin Client tests (Linux)"
            id = "RUNNER_193"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "modules/platforms/cpp"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                if [ -d "thin-client-test" ]; then
                  ./thin-client-test/ignite-thin-client-tests || echo "Some tests failed"
                fi
            """.trimIndent()
        }
        script {
            name = "Move examples (Linux)"
            id = "RUNNER_26"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                mkdir -pv platforms/cpp
                cp -rfv modules/platforms/cpp/examples platforms/cpp
            """.trimIndent()
        }
        script {
            name = "Run Put-Get example"
            id = "RUNNER_27"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "platforms/cpp/examples/put-get-example"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                ./ignite-put-get-example
            """.trimIndent()
        }
        script {
            name = "Run Query example"
            id = "RUNNER_28"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "platforms/cpp/examples/query-example"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                ./ignite-query-example
            """.trimIndent()
        }
        script {
            name = "Run ODBC example"
            id = "RUNNER_29"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "platforms/cpp/examples/odbc-example"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                ./ignite-odbc-example
            """.trimIndent()
        }
        script {
            name = "Run Continuous Query example"
            id = "RUNNER_30"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "platforms/cpp/examples/continuous-query-example"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                ./ignite-continuous-query-example
            """.trimIndent()
        }
        script {
            name = "Run Compute example"
            id = "RUNNER_31"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "platforms/cpp/examples/compute-example"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                ./ignite-compute-example
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_18", "RUNNER_20", "RUNNER_23", "RUNNER_24", "RUNNER_193", "RUNNER_26", "RUNNER_27", "RUNNER_28", "RUNNER_29", "RUNNER_30", "RUNNER_31", "RUNNER_225", "RUNNER_265", "RUNNER_266")
    }

    requirements {
        startsWith("teamcity.agent.name", "aitc", "RQ_41")
    }
    
    disableSettings("RUNNER_225", "RUNNER_265")
})
