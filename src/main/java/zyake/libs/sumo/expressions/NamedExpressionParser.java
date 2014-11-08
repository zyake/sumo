package zyake.libs.sumo.expressions;

import zyake.libs.sumo.ParamBuilder;
import zyake.libs.sumo.QueryExpression;
import zyake.libs.sumo.SQL;
import zyake.libs.sumo.SQLRuntimeException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * An object that interprets named parameters.
 *
 * <p>
 *     An example of a named query is as follows.
 * </p>
 * <pre>
 *     SELECT USER_NAME, USER_ID, PHONE_NUMBER FROM USER WHERE USER_NAME = {USER_NAME}
 * </pre>
 *
 * <p>
 *     The named parameter "{USER_NAME}" wil be interpreted as a JDBC parameter "?" and
 *     the result {@link zyake.libs.sumo.QueryExpression} object can resolve a parameter position by name
 *     when the {@link zyake.libs.sumo.QueryExpression#evaluate(java.sql.PreparedStatement, zyake.libs.sumo.ParamBuilder)}
 *     method was invoked.
 * </p>
 */
public class NamedExpressionParser implements ExpressionParser {

    @Override
    public QueryExpression parse(String sqlQuery, SQL.RowMapper mapper) throws ExpressionParseFailedException {
        List<String> parameters = new ArrayList<>();
        StringBuilder parsedQuery = new StringBuilder();
        Positions currentPos = Positions.OnOther;
        for ( int i = 0 ; i < sqlQuery.length() ; i ++ ) {
            char currentChar = sqlQuery.charAt(i);
            switch ( currentPos ) {
                case OnOther:
                    boolean  startDoubleQuote = '"' == currentChar;
                    if ( startDoubleQuote ) {
                        currentPos = Positions.OnDoubleQuoted;
                        parsedQuery.append(currentChar);
                        continue;
                    }

                    boolean startOnSingleQuote = '\'' == currentChar;
                    if ( startOnSingleQuote ) {
                        currentPos = Positions.OnSingleQuoted;
                        parsedQuery.append(currentChar);
                        continue;
                    }

                    boolean startComment = '/' == currentChar;
                    if ( startComment ) {
                        boolean isValidComment =
                                i + 1 < sqlQuery.length() && '*' == sqlQuery.charAt(i + 1);
                        if ( ! isValidComment ) {
                            throw new ExpressionParseFailedException("SQLのコメント形式が不正です。");
                        }
                        currentPos = Positions.OnComment;
                        parsedQuery.append(currentChar);
                        continue;
                    }

                    boolean startParameter = '{' == currentChar;
                    if ( startParameter ) {
                        // read until end parameter clause '}'
                        int paramEndPos = i;
                        while ( sqlQuery.charAt(paramEndPos) != '}' && paramEndPos < sqlQuery.length() ) {
                            paramEndPos ++;
                        }

                        boolean endParamRequired = i == sqlQuery.length() - 1;
                        if ( endParamRequired ) {
                            throw new ExpressionParseFailedException("パラメータの終了文字「}」が見つかりませんでした。");
                        }

                        String parameter = sqlQuery.substring(i + 1, paramEndPos);
                        parameters.add(parameter);
                        parsedQuery.append("?"); // replace with JDBC place holder
                        i  = paramEndPos;

                        continue;
                    }

                    parsedQuery.append(currentChar);
                    break;

                case OnSingleQuoted:
                    boolean isInQuotedText = '\'' != currentChar;
                    if (  isInQuotedText ) {
                        parsedQuery.append(currentChar);
                        continue;
                    }

                    boolean escapeFound = i + 1 < sqlQuery.length() && '\'' == sqlQuery.charAt(i + 1);
                    if ( escapeFound ) {
                        parsedQuery.append("''");
                        i ++;
                        continue;
                    }
                    parsedQuery.append(currentChar);
                    currentPos = Positions.OnOther;

                    break;

                case OnDoubleQuoted:
                    boolean isInDoubleQuotedText = '"' != currentChar;
                    if ( isInDoubleQuotedText ) {
                        parsedQuery.append(currentChar);
                        continue;
                    }

                    boolean doubleQuotedEscapeFound = i + 1 < sqlQuery.length() && '"' == sqlQuery.charAt(i + 1);
                    if ( doubleQuotedEscapeFound ) {
                        parsedQuery.append("\"\"");
                        i ++;
                        continue;
                    }
                    parsedQuery.append(currentChar);
                    currentPos = Positions.OnOther;

                    break;

                case OnComment:
                    boolean isEndComment =
                            '*' == currentChar && i + 1 < sqlQuery.length() && '/' == sqlQuery.charAt(i + 1);
                    if ( isEndComment ) {
                        currentPos = Positions.OnOther;
                        parsedQuery.append("*/");
                        i ++;

                        continue;
                    }

                    parsedQuery.append(currentChar);

                    break;
            }
        }

        return new ParsedExpression(parsedQuery, parameters, mapper);
    }

    private class ParsedExpression implements QueryExpression {

        private final String parsedQuery;

        private final List<String> parameters;

        private final SQL.RowMapper mapper;

        private ParsedExpression(StringBuilder parsedQuery, List<String> parameters, SQL.RowMapper mapper) {
            this.parsedQuery = parsedQuery.toString();
            this.parameters = parameters;
            this.mapper = mapper;
        }

        @Override
        public String getText() {
            return parsedQuery;
        }

        @Override
        public void evaluate(PreparedStatement statement, ParamBuilder builder) {
            int i = 1;
            for ( String parameter : parameters ) {
                Object paramValue = builder.getParams().get(parameter);
                if ( paramValue == null ) {
                    throw new ExpressionParseFailedException(
                            "No parameter \"" + parameter + "\"! : expression=" + getText() + ", params=" + builder.getParams());
                }
                try {
                    statement.setObject(i ++, paramValue);
                } catch (SQLException e) {
                    throw new SQLRuntimeException(e);
                }
            }
        }

        @Override
        public SQL.RowMapper getMapper() {
            return mapper;
        }
    }

    enum Positions {
        OnOther, OnDoubleQuoted, OnSingleQuoted, OnComment
    }
}
