package com.liuhaifeng.sqlink.token;

import com.liuhaifeng.sqlink.error.SQLParserException;

/**
 * Lexer strategy interface.
 * 
 * @author Liu Haifeng
 * 
 */
public interface LexerStrategy {

	boolean isIdentStart(char ch);

	boolean isIdentChar(char ch);

	boolean isQuoteStart(char ch);

	boolean isEscapeChar(char ch);

	boolean isIdentQuoteStart(char ch);

	boolean isNumberStart(char ch);

	boolean isOctalNumber(char ch);

	boolean isDecNumber(char ch);

	boolean isHexNumber(char ch);

	boolean isDelimiterStart(char ch);

	int matchesDelimiter(String str, int start);

	boolean isOperatorStart(char ch);

	int matchesOperator(String str, int start);

	/**
	 * 
	 * @param source
	 *            The source string.
	 * @param offset
	 *            Where the escape char occurs.
	 * @param sb
	 *            To which shall the unescaped string append to.
	 * @return The next position for further parse.
	 * @throws SQLParserException
	 *             Throw this exception when an illegal string was given.
	 */
	int unescapeString(String source, int offset, StringBuffer sb)
			throws SQLParserException;

}
