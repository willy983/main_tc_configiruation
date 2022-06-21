package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_PlatformCCMakeLinuxClang : BuildType({
    templates(DevAi2x_RunTestSuitesJava, DevAi2x_C)
    name = "Platform C++ CMake (Linux Clang)"

    artifactRules = """
        work/log => logs.zip
        **/hs_err*.log => crashdumps.zip
        **/core => crashdumps.zip
        ./**/target/rat.txt => rat.zip
        ./dev-tools/IGNITE-*-*.patch => patch
        /home/teamcity/ignite-startNodes/*.log => ignite-startNodes.zip
    """.trimIndent()

    params {
        param("env.PATH", "/usr/lib/llvm-%CLANG_VERSION%/bin:%env.PATH%")
        param("env.CC", "clang")
        param("env.CXX", "clang++")
        param("CLANG_VERSION", "3.9")
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
                
                mkdir cmake-build-release
                cd cmake-build-release
                
                cmake -DWITH_ODBC=ON -DWITH_THIN_CLIENT=ON -DWITH_TESTS=ON -DWARNINGS_AS_ERRORS=ON -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX=%env.CPP_STAGING% ..
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
                
                mkdir cmake-build-release
                cd cmake-build-release
                
                cmake -DIGNITE_CPP_DIR=%env.CPP_STAGING% -DCMAKE_BUILD_TYPE=Release ..
                make -j4 || exit 1
            """.trimIndent()
        }
        script {
            name = "Run Core tests (Linux)"
            id = "RUNNER_23"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "modules/platforms/cpp/cmake-build-release"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                ctest -V -R IgniteCoreTest || echo "Some tests failed"
            """.trimIndent()
        }
        script {
            name = "Run ODBC tests (Linux)"
            id = "RUNNER_24"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "modules/platforms/cpp/cmake-build-release"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                ctest -V -R IgniteOdbcTest || echo "Some tests failed"
            """.trimIndent()
        }
        script {
            name = "Run Thin Client tests (Linux)"
            id = "RUNNER_193"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "modules/platforms/cpp/cmake-build-release"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                ctest -V -R IgniteThinClientTest || echo "Some tests failed"
            """.trimIndent()
        }
        script {
            name = "Fix example configs"
            id = "RUNNER_55"
            workingDir = "modules/platforms/cpp/examples"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                sed -i 's/class="org.apache.ignite.configuration.IgniteConfiguration">/class="org.apache.ignite.configuration.IgniteConfiguration">\n<property name="localHost" value="127.0.0.1"\/>\n<property name="connectorConfiguration"><null\/><\/property>/' `find -name "*.xml"`
            """.trimIndent()
        }
        script {
            name = "Run Put-Get example"
            id = "RUNNER_27"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "modules/platforms/cpp/examples/cmake-build-release/put-get-example"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                mkdir -pv platforms/cpp/examples/put-get-example
                cp -r ../../put-get-example/config ./platforms/cpp/examples/put-get-example
                
                ./ignite-put-get-example
            """.trimIndent()
        }
        script {
            name = "Run Query example"
            id = "RUNNER_28"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "modules/platforms/cpp/examples/cmake-build-release/query-example"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                mkdir -pv platforms/cpp/examples/query-example
                cp -r ../../query-example/config ./platforms/cpp/examples/query-example
                
                ./ignite-query-example
            """.trimIndent()
        }
        script {
            name = "Run ODBC example"
            id = "RUNNER_29"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "modules/platforms/cpp/examples/cmake-build-release/odbc-example"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                mkdir -pv platforms/cpp/examples/odbc-example
                cp -r ../../odbc-example/config ./platforms/cpp/examples/odbc-example
                
                ./ignite-odbc-example
            """.trimIndent()
        }
        script {
            name = "Run Continuous Query example"
            id = "RUNNER_30"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "modules/platforms/cpp/examples/cmake-build-release/continuous-query-example"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                mkdir -pv platforms/cpp/examples/continuous-query-example
                cp -r ../../continuous-query-example/config ./platforms/cpp/examples/continuous-query-example
                
                ./ignite-continuous-query-example
            """.trimIndent()
        }
        script {
            name = "Run Compute example"
            id = "RUNNER_31"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "modules/platforms/cpp/examples/cmake-build-release/compute-example"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                
                mkdir -pv platforms/cpp/examples/compute-example
                cp -r ../../compute-example/config ./platforms/cpp/examples/compute-example
                
                ./ignite-compute-example
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_264", "RUNNER_287", "RUNNER_225", "RUNNER_18", "RUNNER_20", "RUNNER_23", "RUNNER_24", "RUNNER_193", "RUNNER_55", "RUNNER_27", "RUNNER_28", "RUNNER_29", "RUNNER_30", "RUNNER_31", "RUNNER_265", "RUNNER_266")
    }

    requirements {
        startsWith("teamcity.agent.name", "aitc", "RQ_33")
    }
    
    disableSettings("RUNNER_265")
})
