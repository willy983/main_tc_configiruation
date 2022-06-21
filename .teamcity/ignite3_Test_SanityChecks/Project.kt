package ignite3_Test_SanityChecks

import ignite3_Test_SanityChecks.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("ignite3_Test_SanityChecks")
    name = "[Sanity Checks]"

    buildType(ignite3_Test_SanityChecks_DevInspections)
    buildType(ignite3_Test_SanityChecks_Pmd)
    buildType(ignite3_Test_SanityChecks_Maven)
    buildType(ignite3_Test_SanityChecks_LicensesHeaders)
    buildType(ignite3_Test_SanityChecks_CodeStyle)
    buildType(ignite3_Test_SanityChecks_Javadoc)
    buildType(ignite3_Test_SanityChecks_Inspections)
    buildType(ignite3_Test_SanityChecks_LegacyApi)
})
