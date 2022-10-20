/**
 * Z PL/SQL Analyzer
 * Copyright (C) 2015-2020 Felipe Zorzo
 * mailto:felipebzorzo AT gmail DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plsqlopen

import org.sonar.api.batch.rule.ActiveRules
import org.sonar.api.batch.sensor.Sensor
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.SensorDescriptor
import org.sonar.api.config.Configuration
import org.sonar.api.issue.NoSonarFilter
import org.sonar.api.measures.FileLinesContextFactory
import org.sonar.plsqlopen.checks.CheckList
import org.sonar.plsqlopen.metadata.FormsMetadata
import org.sonar.plsqlopen.rules.SonarQubeActiveRulesAdapter
import org.sonar.plsqlopen.rules.SonarQubeRuleMetadataLoader
import org.sonar.plsqlopen.squid.PlSqlAstScanner
import org.sonar.plsqlopen.squid.ProgressReport
import org.sonar.plugins.plsqlopen.api.ZpaRulesDefinition
import java.util.concurrent.TimeUnit

class PlSqlSquidSensor @JvmOverloads constructor(activeRules: ActiveRules, settings: Configuration, private val noSonarFilter: NoSonarFilter,
                                                 private val fileLinesContextFactory: FileLinesContextFactory,
                                                 customRulesDefinition: Array<ZpaRulesDefinition>? = null) : Sensor {

    private val checks = PlSqlChecks.createPlSqlCheck(SonarQubeActiveRulesAdapter(activeRules), SonarQubeRuleMetadataLoader())
            .addChecks(PlSqlRuleRepository.KEY, CheckList.checks)
            .addCustomChecks(customRulesDefinition)

    private val isErrorRecoveryEnabled = settings.getBoolean(PlSqlPlugin.ERROR_RECOVERY_KEY).orElse(false)

    private val formsMetadata = FormsMetadata.loadFromFile(settings.get(PlSqlPlugin.FORMS_METADATA_KEY)
        .orElse(null))

    override fun describe(descriptor: SensorDescriptor) {
        descriptor.name("Z PL/SQL Analyzer").onlyOnLanguage(PlSql.KEY)
    }

    override fun execute(context: SensorContext) {
        val p = context.fileSystem().predicates()
        val inputFiles = context.fileSystem().inputFiles(p.hasLanguage(PlSql.KEY))

        val progressReport = ProgressReport("Report about progress of code analyzer", TimeUnit.SECONDS.toMillis(10))
        val scanner = PlSqlAstScanner(context, checks, noSonarFilter, formsMetadata, isErrorRecoveryEnabled, fileLinesContextFactory)

        progressReport.start(inputFiles.map { it.toString() }.toList())
        for (inputFile in inputFiles) {
            scanner.scanFile(inputFile)

            progressReport.nextFile()
        }
        progressReport.stop()
    }

}
