package IgniteTests24Java8.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.ideaInspections

object IgniteTests24Java8_InspectionsCore : BuildType({
    templates(IgniteTests24Java8_RunIntelliJIdeaInspectionsTemplate)
    name = "[Inspections] Core"

    params {
        param("MODULE_PATH", "modules/core")
        text("JVM_PARAMETERS", "-Didea.log.path=%system.teamcity.build.tempDir%/idea-logs/", display = ParameterDisplay.HIDDEN, allowEmpty = true)
    }

    steps {
        ideaInspections {
            id = "RUNNER_246"
            pathToProject = "%MODULE_PATH%/pom.xml"
            jvmArgs = """
                -Xms2G
                -Xmx4G
                -XX:ReservedCodeCacheSize=240m
                -XX:+UseG1GC
                %JVM_PARAMETERS%
            """.trimIndent()
            ideaAppHome = "%teamcity.tool.intellij.DEFAULT%"
            disabledPlugins = """
                AntSupport
                CVS
                ClearcasePlugin
                Coverage
                DevKit
                Emma
                GenerateToString
                Geronimo
                Glassfish
                Guice
                HtmlTools
                IdeaServerPlugin
                Inspection-JS
                InspectionGadgets
                IntentionPowerPack
                J2ME
                Java EE: Web Services (JAX-WS)
                JBoss
                JSIntentionPowerPack
                JSR45Plugin
                JSTestDriver Plugin
                JUnit
                JavaScript
                JavaScriptDebugger
                Jetty
                NodeJS
                Osmorc
                PerforceDirectPlugin
                Pythonid
                QuirksMode
                Refactor-X
                Resin
                SourceSafe
                StrutsAssistant
                Subversion
                TFS
                TestNG-J
                Tomcat
                Type Migration
                W3Validators
                WebServicesPlugin
                WebSphere
                Weblogic
                XPathView
                XSLT-Debugger
                ZKM
                com.android.tools.idea.smali
                com.intellij.aop
                com.intellij.apacheConfig
                com.intellij.appengine
                com.intellij.aspectj
                com.intellij.beanValidation
                com.intellij.cdi
                com.intellij.commander
                com.intellij.copyright
                com.intellij.css
                com.intellij.database
                com.intellij.diagram
                com.intellij.dmserver
                com.intellij.dsm
                com.intellij.flex
                com.intellij.freemarker
                com.intellij.guice
                com.intellij.gwt
                com.intellij.hibernate
                com.intellij.java-i18n
                com.intellij.java.cucumber
                com.intellij.javaee
                com.intellij.javaee.view
                com.intellij.jsf
                com.intellij.jsp
                com.intellij.persistence
                com.intellij.phing
                com.intellij.seam
                com.intellij.seam.pageflow
                com.intellij.seam.pages
                com.intellij.spring
                com.intellij.spring.batch
                com.intellij.spring.data
                com.intellij.spring.integration
                com.intellij.spring.osgi
                com.intellij.spring.roo
                com.intellij.spring.security
                com.intellij.spring.webflow
                com.intellij.spring.ws
                com.intellij.struts2
                com.intellij.tapestry
                com.intellij.tasks
                com.intellij.tcserver
                com.intellij.uiDesigner
                com.intellij.velocity
                com.jetbrains.jarFinder
                com.jetbrains.php
                com.jetbrains.php.framework
                com.jetbrains.plugins.asp
                com.jetbrains.plugins.webDeployment
                hg4idea
                org.coffeescript
                org.intellij.grails
                org.intellij.groovy
                org.intellij.intelliLang
                org.jetbrains.android
                org.jetbrains.idea.eclipse
                org.jetbrains.idea.maven.ext
                org.jetbrains.kotlin
                org.jetbrains.plugins.django-db-config
                org.jetbrains.plugins.github
                org.jetbrains.plugins.gradle
                org.jetbrains.plugins.haml
                org.jetbrains.plugins.less
                org.jetbrains.plugins.ruby
                org.jetbrains.plugins.sass
                org.jetbrains.plugins.yaml
            """.trimIndent()
            profilePath = "idea/ignite_inspections_teamcity.xml"
        }
        stepsOrder = arrayListOf("RUNNER_59", "RUNNER_246")
    }
})
