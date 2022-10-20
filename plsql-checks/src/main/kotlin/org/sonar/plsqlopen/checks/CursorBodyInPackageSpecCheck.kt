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
package org.sonar.plsqlopen.checks

import com.sonar.sslr.api.AstNode
import org.sonar.plugins.plsqlopen.api.DmlGrammar
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar
import org.sonar.plugins.plsqlopen.api.annotations.ConstantRemediation
import org.sonar.plugins.plsqlopen.api.annotations.Priority
import org.sonar.plugins.plsqlopen.api.annotations.Rule
import org.sonar.plugins.plsqlopen.api.annotations.RuleInfo
import org.sonar.plugins.plsqlopen.api.symbols.Symbol.Kind

@Rule(key = CursorBodyInPackageSpecCheck.CHECK_KEY, priority = Priority.MAJOR)
@ConstantRemediation("10min")
@RuleInfo(scope = RuleInfo.Scope.MAIN)
class CursorBodyInPackageSpecCheck : AbstractBaseCheck() {

    override fun init() {
        subscribeTo(PlSqlGrammar.CREATE_PACKAGE)
    }

    override fun visitNode(node: AstNode) {
        val cursors = context.currentScope?.getSymbols(Kind.CURSOR) ?: emptyList()
        for (cursor in cursors) {
            val cursorDeclaration = cursor.declaration().parent
            if (cursorDeclaration.hasDirectChildren(DmlGrammar.SELECT_EXPRESSION)) {
                addIssue(cursorDeclaration, getLocalizedMessage(CHECK_KEY), cursor.name())
            }
        }
    }

    companion object {
        internal const val CHECK_KEY = "CursorBodyInPackageSpec"
    }

}
