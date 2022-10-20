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
package org.sonar.plugins.plsqlopen.api

import org.sonar.plsqlopen.sslr.PlSqlGrammarBuilder
import org.sonar.plugins.plsqlopen.api.DmlGrammar.ORDER_BY_CLAUSE
import org.sonar.plugins.plsqlopen.api.PlSqlGrammar.*
import org.sonar.plugins.plsqlopen.api.PlSqlKeyword.*
import org.sonar.plugins.plsqlopen.api.PlSqlPunctuator.*
import org.sonar.plugins.plsqlopen.api.PlSqlTokenType.STRING_LITERAL
import org.sonar.sslr.grammar.GrammarRuleKey

enum class SingleRowSqlFunctionsGrammar : GrammarRuleKey {

    // internals
    XML_COLUMN,
    XML_NAMESPACE,
    XMLNAMESPACES_CLAUSE,
    XMLTABLE_OPTIONS,
    XML_PASSING_CLAUSE,
    XML_TABLE_COLUMN,

    // functions
    EXTRACT_DATETIME_EXPRESSION,
    XMLATTRIBUTES_EXPRESSION,
    XMLELEMENT_EXPRESSION,
    XMLFOREST_EXPRESSION,
    XMLSERIALIZE_EXPRESSION,
    XMLAGG_EXPRESSION,
    XMLEXISTS_EXPRESSION,
    XMLQUERY_EXPRESSION,
    XMLROOT_EXPRESSION,
    XMLCAST_EXPRESSION,
    XMLCOLATTVAL_EXPRESSION,
    XMLPARSE_EXPRESSION,
    XMLPI_EXPRESSION,
    XMLTABLE_EXPRESSION,
    CAST_EXPRESSION,
    TRIM_EXPRESSION,
    SINGLE_ROW_SQL_FUNCTION;

    companion object {
        fun buildOn(b: PlSqlGrammarBuilder) {
            createCharacterFunctions(b)
            createConversionFunctions(b)
            createDateFunctions(b)
            createXmlFunctions(b)

            b.rule(SINGLE_ROW_SQL_FUNCTION).define(b.firstOf(
                    EXTRACT_DATETIME_EXPRESSION,
                    XMLATTRIBUTES_EXPRESSION,
                    XMLELEMENT_EXPRESSION,
                    XMLFOREST_EXPRESSION,
                    XMLSERIALIZE_EXPRESSION,
                    XMLAGG_EXPRESSION,
                    XMLEXISTS_EXPRESSION,
                    XMLQUERY_EXPRESSION,
                    XMLROOT_EXPRESSION,
                    XMLCAST_EXPRESSION,
                    XMLCOLATTVAL_EXPRESSION,
                    XMLPARSE_EXPRESSION,
                    XMLPI_EXPRESSION,
                    XMLTABLE_EXPRESSION,
                    CAST_EXPRESSION,
                    TRIM_EXPRESSION)).skip()
        }

        private fun createCharacterFunctions(b: PlSqlGrammarBuilder) {
            b.rule(TRIM_EXPRESSION).define(
                    TRIM, LPARENTHESIS,
                    b.optional(b.optional(b.firstOf(LEADING, TRAILING, BOTH)), EXPRESSION, FROM),
                    EXPRESSION, RPARENTHESIS)
        }

        private fun createConversionFunctions(b: PlSqlGrammarBuilder) {
            b.rule(CAST_EXPRESSION).define(
                    CAST, LPARENTHESIS,
                    b.firstOf(b.sequence(MULTISET, EXPRESSION), EXPRESSION),
                    AS, DATATYPE,
                    RPARENTHESIS)
        }

        private fun createDateFunctions(b: PlSqlGrammarBuilder) {
            b.rule(EXTRACT_DATETIME_EXPRESSION).define(EXTRACT, LPARENTHESIS, IDENTIFIER_NAME, FROM, EXPRESSION, RPARENTHESIS)
        }

        private fun createXmlFunctions(b: PlSqlGrammarBuilder) {
            b.rule(XMLSERIALIZE_EXPRESSION).define(
                    XMLSERIALIZE, LPARENTHESIS,
                    b.firstOf(DOCUMENT, CONTENT), EXPRESSION,
                    b.optional(AS, DATATYPE),
                    b.optional(ENCODING, EXPRESSION),
                    b.optional(VERSION, STRING_LITERAL),
                    b.optional(b.firstOf(b.sequence(NO, INDENT), b.sequence(INDENT, b.optional(SIZE, EQUALS, EXPRESSION)))),
                    b.optional(b.firstOf(HIDE, SHOW), DEFAULTS),
                    RPARENTHESIS)

            b.rule(XML_COLUMN).define(
                    EXPRESSION, b.optional(b.optional(AS), b.firstOf(b.sequence(EVALNAME, EXPRESSION), IDENTIFIER_NAME)))

            b.rule(XMLATTRIBUTES_EXPRESSION).define(
                    XMLATTRIBUTES, LPARENTHESIS,
                    b.optional(b.firstOf(ENTITYESCAPING, NOENTITYESCAPING)),
                    b.optional(b.firstOf(SCHEMACHECK, NOSCHEMACHECK)),
                    XML_COLUMN, b.zeroOrMore(COMMA, XML_COLUMN),
                    RPARENTHESIS)

            b.rule(XMLELEMENT_EXPRESSION).define(
                    XMLELEMENT, LPARENTHESIS,
                    b.optional(b.firstOf(ENTITYESCAPING, NOENTITYESCAPING)),
                    b.firstOf(
                            b.sequence(EVALNAME, EXPRESSION),
                            b.sequence(b.optional(NAME), IDENTIFIER_NAME)
                    ),
                    b.optional(COMMA, XMLATTRIBUTES_EXPRESSION),
                    b.zeroOrMore(COMMA, EXPRESSION, b.optional(b.optional(AS), IDENTIFIER_NAME)),
                    RPARENTHESIS)

            b.rule(XMLAGG_EXPRESSION).define(
                    XMLAGG, LPARENTHESIS,
                    EXPRESSION, b.optional(ORDER_BY_CLAUSE),
                    RPARENTHESIS)

            b.rule(XMLFOREST_EXPRESSION).define(
                    XMLFOREST, LPARENTHESIS,
                    XML_COLUMN, b.zeroOrMore(COMMA, XML_COLUMN),
                    RPARENTHESIS)

            b.rule(XML_PASSING_CLAUSE).define(
                    PASSING, b.optional(BY, VALUE),
                    EXPRESSION, b.optional(AS, IDENTIFIER_NAME),
                    b.zeroOrMore(COMMA, IDENTIFIER_NAME, b.optional(AS, IDENTIFIER_NAME)))

            b.rule(XMLEXISTS_EXPRESSION).define(
                    XMLEXISTS, LPARENTHESIS, STRING_LITERAL,
                    b.optional(XML_PASSING_CLAUSE),
                    RPARENTHESIS)

            b.rule(XMLQUERY_EXPRESSION).define(
                    XMLQUERY, LPARENTHESIS, STRING_LITERAL,
                    b.optional(XML_PASSING_CLAUSE),
                    RETURNING, CONTENT,
                    b.optional(NULL, ON, EMPTY),
                    RPARENTHESIS)

            b.rule(XMLROOT_EXPRESSION).define(
                    XMLROOT, LPARENTHESIS,
                    EXPRESSION, COMMA,
                    VERSION, b.firstOf(b.sequence(NO, VALUE), EXPRESSION),
                    b.optional(COMMA, STANDALONE, b.firstOf(YES, b.sequence(NO, b.optional(VALUE)))),
                    RPARENTHESIS)

            b.rule(XMLCAST_EXPRESSION).define(
                    XMLCAST, LPARENTHESIS,
                    EXPRESSION, AS, DATATYPE,
                    RPARENTHESIS)

            b.rule(XMLCOLATTVAL_EXPRESSION).define(
                    XMLCOLATTVAL, LPARENTHESIS,
                    XML_COLUMN, b.zeroOrMore(COMMA, XML_COLUMN),
                    RPARENTHESIS)

            b.rule(XMLPARSE_EXPRESSION).define(
                    XMLPARSE, LPARENTHESIS,
                    b.firstOf(DOCUMENT, CONTENT), EXPRESSION, b.optional(WELLFORMED),
                    RPARENTHESIS)

            b.rule(XMLPI_EXPRESSION).define(
                    XMLPI, LPARENTHESIS,
                    b.firstOf(
                            b.sequence(EVALNAME, EXPRESSION),
                            b.sequence(b.optional(NAME), IDENTIFIER_NAME)),
                    b.optional(COMMA, EXPRESSION),
                    RPARENTHESIS)

            b.rule(XML_NAMESPACE).define(
                    b.firstOf(
                            b.sequence(DEFAULT, STRING_LITERAL),
                            b.sequence(STRING_LITERAL, AS, IDENTIFIER_NAME)))

            b.rule(XMLNAMESPACES_CLAUSE).define(
                    XMLNAMESPACES, LPARENTHESIS,
                    XML_NAMESPACE, b.zeroOrMore(COMMA, XML_NAMESPACE),
                    RPARENTHESIS)

            b.rule(XML_TABLE_COLUMN).define(
                    IDENTIFIER_NAME,
                    b.firstOf(
                            b.sequence(FOR, ORDINALITY),
                            b.sequence(
                                    b.firstOf(
                                            DATATYPE,
                                            b.sequence(XMLTYPE, b.optional(LPARENTHESIS, SEQUENCE, RPARENTHESIS, BY, REF))),
                                    b.optional(PATH, STRING_LITERAL),
                                    b.optional(DEFAULT, STRING_LITERAL))))

            b.rule(XMLTABLE_OPTIONS).define(
                    b.optional(XML_PASSING_CLAUSE),
                    b.optional(RETURNING, SEQUENCE, BY, REF),
                    b.optional(COLUMNS, XML_TABLE_COLUMN, b.zeroOrMore(COMMA, XML_TABLE_COLUMN)))

            b.rule(XMLTABLE_EXPRESSION).define(
                    XMLTABLE, LPARENTHESIS,
                    b.optional(XMLNAMESPACES_CLAUSE, COMMA), STRING_LITERAL, XMLTABLE_OPTIONS,
                    RPARENTHESIS)
        }
    }

}
