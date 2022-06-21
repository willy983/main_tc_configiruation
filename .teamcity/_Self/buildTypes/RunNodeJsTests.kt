package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

object RunNodeJsTests : Template({
    name = "Run Node.js tests"
    description = "Installs Node.js and runs main tests"

    params {
        text("NODEJS_VERSION", "8.11.2", label = "Node.js Version", allowEmpty = true)
        text("VCS_ROOT_NODEJS", "ignite-nodejs-thin-client", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("env.PATH", "%system.teamcity.build.checkoutDir%/%VCS_ROOT_NODEJS%/node/bin:%env.PATH%", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        script {
            name = "Install Node.js"
            id = "RUNNER_84"
            workingDir = "%VCS_ROOT_NODEJS%"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
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
            name = "Run Node.js tests: basic"
            id = "RUNNER_144"
            workingDir = "%VCS_ROOT_NODEJS%"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Run basic tests
                npm test
            """.trimIndent()
        }
        script {
            name = "Run Node.js test: partition_awareness"
            id = "RUNNER_145"
            workingDir = "%VCS_ROOT_NODEJS%"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Two (or more)-node cluster
                npm run test:partition_awareness
            """.trimIndent()
        }
        script {
            name = "Run Node.js tests: examples"
            id = "RUNNER_146"
            workingDir = "%VCS_ROOT_NODEJS%"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Run Apache Ignite nodes
                bash %env.IGNITE_HOME%/bin/ignite.sh &
                sleep %IGNITE_INIT_TIME%
                
                
                # Run examples tests
                npm run test:examples || true
                
                
                # Stop Apache Ignite nodes
                ps ax | grep ignite.sh | sed '${'$'}d' | awk {'print ${'$'}1'} | while read pid; do
                    kill -9 ${'$'}{pid} || true
                done
            """.trimIndent()
        }
        script {
            name = "Run Node.js tests: TLS"
            id = "RUNNER_147"
            workingDir = "%VCS_ROOT_NODEJS%"
            scriptContent = """
                #!/usr/bin/env bash
                set -o nounset; set -o errexit; set -o pipefail; set -o errtrace; set -o functrace
                set -x
                
                
                # Run Apache Ignite nodes
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
                                        <property name="keyStoreFilePath" value="%system.teamcity.build.checkoutDir%/ignite-nodejs-thin-client/examples/certs/keystore.jks"/>
                                        <property name="keyStorePassword" value="123456"/>
                                        <property name="trustStoreFilePath" value="%system.teamcity.build.checkoutDir%/ignite-nodejs-thin-client/examples/certs/truststore.jks"/>
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
                npm run test:auth_example || true
                
                
                # Stop Apache Ignite nodes
                ps ax | grep ignite.sh | sed '${'$'}d' | awk {'print ${'$'}1'} | while read pid; do
                    kill -9 ${'$'}{pid}
                done
            """.trimIndent()
        }
    }
})
