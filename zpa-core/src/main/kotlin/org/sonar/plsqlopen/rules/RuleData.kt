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
package org.sonar.plsqlopen.rules

import org.sonar.plugins.plsqlopen.api.annotations.Priority
import org.sonar.plugins.plsqlopen.api.annotations.Rule

class RuleData(val key: String,
               val name: String,
               val description: String,
               val priority: Priority,
               val tags: Array<String>,
               val status: String) {
    companion object {
        fun from(rule: Rule?): RuleData? =
            if (rule == null) null
            else RuleData(rule.key,
                rule.name,
                rule.description,
                rule.priority,
                rule.tags,
                rule.status)
    }
}
