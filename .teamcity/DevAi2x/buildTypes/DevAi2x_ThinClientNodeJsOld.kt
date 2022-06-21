package DevAi2x.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object DevAi2x_ThinClientNodeJsOld : BuildType({
    templates(DevAi2x_ExcludeTests, DevAi2x_PreBuild, _Self.buildTypes.ThinClientStartIgnite, _Self.buildTypes.ThinClientStopIgnite, DevAi2x_PostBuild)
    name = "Thin client: Node.js (old)"

    artifactRules = """
        work/log/** => logs.zip
        ignite-nodejs-thin-client/** => ignite-nodejs-thin-client.zip
    """.trimIndent()

    params {
        text("env.PATH", "%system.teamcity.build.checkoutDir%/modules/platforms/nodejs/node/bin:%env.PATH%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("ACTUAL_VERSION", "%dep.DevAi2x_Build_2.ACTUAL_VERSION%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("NODEJS_VERSION", "8.11.2", label = "Node.js Version", allowEmpty = true)
        text("SKIP_BUILD_CONDITION", """! -d "modules/platforms/nodejs"""", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        param("VCS_ROOT_NODEJS", "ignite-nodejs-thin-client")
    }

    vcs {
        checkoutMode = CheckoutMode.ON_AGENT
    }

    steps {
        script {
            name = "Install Node.js"
            id = "RUNNER_178"
            workingDir = "modules/platforms/nodejs"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                if %system.SKIP_BUILD%; then exit 0; fi
                
                
                # Get and install Node
                wget -c https://nodejs.org/dist/v%NODEJS_VERSION%/node-v%NODEJS_VERSION%-linux-x64.tar.xz
                tar xf node-v%NODEJS_VERSION%-linux-x64.tar.xz
                mv -v node-v%NODEJS_VERSION%-linux-x64 node
                
                # Install AI Node.js Thin Client module
                npm link
                npm link apache-ignite-client
            """.trimIndent()
        }
        script {
            name = "Run Node.js tests: basic | examples"
            id = "RUNNER_186"
            workingDir = "modules/platforms/nodejs"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                if %system.SKIP_BUILD%; then exit 0; fi
                
                
                # Run Apache Ignite node
                #bash %system.teamcity.build.checkoutDir%/bin/ignite.sh &
                #sleep %IGNITE_INIT_TIME%
                
                # Run basic tests
                npm test
                
                # Run examples tests
                npm run test:examples
                
                # Stop Apache Ignite node
                #ps ax | grep ignite.sh | sed '${'$'}d' | awk {'print ${'$'}1'} | while read pid; do
                #    kill -9 ${'$'}{pid}
                #done
            """.trimIndent()
        }
        script {
            name = "Run Node.js tests: TLS"
            id = "RUNNER_187"
            workingDir = "modules/platforms/nodejs"
            scriptContent = """
                #!/usr/bin/env bash
                set -x
                if %system.SKIP_BUILD%; then exit 0; fi
                
                
                # Run Apache Ignite node
                cat <<EOF > %system.teamcity.build.checkoutDir%/config/default-config.xml
                <?xml version="1.0" encoding="UTF-8"?>
                <beans xmlns="http://www.springframework.org/schema/beans"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xsi:schemaLocation="http://www.springframework.org/schema/beans
                                            http://www.springframework.org/schema/beans/spring-beans.xsd">
                    <bean id="ignite.cfg" class="org.apache.ignite.configuration.IgniteConfiguration">
                        <property name="authenticationEnabled" value="true"/>
                
                        <property name="dataStorageConfiguration">
                            <bean class="org.apache.ignite.configuration.DataStorageConfiguration">
                                <property name="defaultDataRegionConfiguration">
                                    <bean class="org.apache.ignite.configuration.DataRegionConfiguration">
                                        <property name="persistenceEnabled" value="true"/>
                                    </bean>
                                </property>
                            </bean>
                        </property>
                
                        <property name="clientConnectorConfiguration">
                            <bean class="org.apache.ignite.configuration.ClientConnectorConfiguration">
                                <property name="host" value="127.0.0.1"/>
                                <property name="port" value="10800"/>
                                <property name="sslEnabled" value="true"/>
                                <property name="useIgniteSslContextFactory" value="false"/>
                                <property name="sslClientAuth" value="true"/>
                
                                <property name="sslContextFactory">
                                    <bean class="org.apache.ignite.ssl.SslContextFactory">
                                        <property name="keyStoreFilePath" value="%system.teamcity.build.checkoutDir%/modules/platforms/nodejs/examples/certs/keystore.jks"/>
                                        <property name="keyStorePassword" value="123456"/>
                                        <property name="trustStoreFilePath" value="%system.teamcity.build.checkoutDir%/modules/platforms/nodejs/examples/certs/truststore.jks"/>
                                        <property name="trustStorePassword" value="123456"/>
                                    </bean>
                                </property>
                            </bean>
                        </property>
                    </bean>
                </beans>
                EOF
                bash %system.teamcity.build.checkoutDir%/bin/ignite.sh &
                sleep %IGNITE_INIT_TIME%
                bash %system.teamcity.build.checkoutDir%/bin/control.sh --user ignite --password ignite --activate
                sleep %IGNITE_INIT_TIME%
                
                # Run auth examples tests
                npm run test:auth_example
                
                # Stop Apache Ignite node
                ps ax | grep ignite.sh | sed '${'$'}d' | awk {'print ${'$'}1'} | while read pid; do
                    kill -9 ${'$'}{pid}
                done
            """.trimIndent()
        }
        stepsOrder = arrayListOf("RUNNER_159", "RUNNER_264", "RUNNER_287", "RUNNER_178", "RUNNER_3", "RUNNER_186", "RUNNER_12", "RUNNER_187", "RUNNER_266")
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    requirements {
        equals("teamcity.agent.jvm.os.name", "Linux", "RQ_35")
    }
})
