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
package org.sonar.plugins.plsqlopen.api.squid

object PlSqlCommentAnalyzer {

    fun isBlank(line: String) = line.isBlank()

    fun getContents(comment: String) =
        if (comment.startsWith("--")) {
            comment.substring(2)
        } else if (comment.startsWith("/*")) {
            if (comment.endsWith("*/")) {
                comment.substring(2, comment.length - 2)
            } else comment.substring(2)
        } else {
            throw IllegalArgumentException()
        }

}
