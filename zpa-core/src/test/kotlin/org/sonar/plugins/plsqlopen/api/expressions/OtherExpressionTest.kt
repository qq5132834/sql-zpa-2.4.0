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
package org.sonar.plugins.plsqlopen.api.expressions

import org.junit.Before
import org.junit.Test
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar
import org.sonar.plugins.plsqlopen.api.RuleTest
import org.sonar.sslr.tests.Assertions.assertThat

class OtherExpressionTest : RuleTest() {

    @Before
    fun init() {
        setRootRule(PlSqlGrammar.EXPRESSION)
    }

    @Test
    fun matchesCollectionExists() {
        assertThat(p).matches("collection.exists(0)")
    }

    @Test
    fun matchesCursorMethods() {
        assertThat(p).matches("cur%found")
        assertThat(p).matches("cur%notfound")
        assertThat(p).matches("cur%isopen")
    }

    @Test
    fun matchesHostCursorMethods() {
        assertThat(p).matches(":cur%found")
        assertThat(p).matches(":cur%notfound")
        assertThat(p).matches(":cur%isopen")
    }

    @Test
    fun matchesSqlMethods() {
        assertThat(p).matches("sql%found")
        assertThat(p).matches("sql%notfound")
        assertThat(p).matches("sql%isopen")
    }

    @Test
    fun matchesIsNull() {
        assertThat(p).matches("var is null")
    }

    @Test
    fun matchesIsNotNull() {
        assertThat(p).matches("var is not null")
    }

    @Test
    fun matchesBasicIn() {
        assertThat(p).matches("var in (1)")
    }

    @Test
    fun matchesBasicInWithMultipleValues() {
        assertThat(p).matches("var in (1, 2, 3)")
    }

    @Test
    fun matchesBasicInWithoutParenthesis() {
        assertThat(p).matches("var in 1")
    }

    @Test
    fun matchesMultidimensionalCollection() {
        assertThat(p).matches("foo(1)(1)")
    }

}
