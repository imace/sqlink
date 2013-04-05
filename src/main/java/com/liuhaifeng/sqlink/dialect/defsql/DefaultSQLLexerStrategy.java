package com.liuhaifeng.sqlink.dialect.defsql;

import com.liuhaifeng.sqlink.error.SQLParserException;
import com.liuhaifeng.sqlink.token.LexerStrategy;

/**
 * Default SQL Lexer Strategy.
 * 
 * @author Liu Haifeng
 */
public class DefaultSQLLexerStrategy implements LexerStrategy {

	public static final String[] DELIMITERS = { "{", "}", "(", ")", ",", "#",
			";" };
	public static final String[] OPERATORS = { "++", "--", "+=", "-=", "*=",
			"/=", "%=", "&=", "|=", ">=", "<=", "==", "!=", "<>", "&&", "||",
			"[]", "~", "`", "!", "@", "#", "$", "%", "^", "&", "*", "-", "+",
			"=", "|", "/", ">", "<", ":", ".", "?", "[", "]" };

	public static DefaultSQLLexerStrategy INSTANCE = new DefaultSQLLexerStrategy();

	public static DefaultSQLLexerStrategy getInstance() {
		return INSTANCE;
	}

	public boolean isIdentStart(char ch) {
		return ch == '_' || (ch >= 'a' && ch <= 'z')
				|| (ch >= 'A' && ch <= 'Z');
	}

	public boolean isIdentChar(char ch) {
		return isIdentStart(ch) || (ch >= '0' && ch <= '9');
	}

	public boolean isQuoteStart(char ch) {
		return ch == '"' || ch == '\'';
	}

	public boolean isIdentQuoteStart(char ch) {
		return ch == '`';
	}

	public boolean isEscapeChar(char ch) {
		return ch == '\\';
	}

	public boolean isNumberStart(char ch) {
		return ch == '.' || (ch >= '0' && ch <= '9');
	}

	public boolean isOctalNumber(char ch) {
		return ch >= '0' && ch <= '7';
	}

	public boolean isDecNumber(char ch) {
		return ch >= '0' && ch <= '9';
	}

	public boolean isHexNumber(char ch) {
		return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f')
				|| (ch >= 'A' && ch <= 'F');
	}

	public boolean isDelimiterStart(char ch) {
		for (String str : getDelimiters()) {
			if (str.charAt(0) == ch)
				return true;
		}

		return false;
	}

	public int matchesDelimiter(String str, int start) {
		for (String del : getDelimiters()) {
			if (del.regionMatches(0, str, start, del.length()))
				return start + del.length();
		}

		return -1;
	}

	public boolean isOperatorStart(char ch) {
		for (String str : getOperators()) {
			if (str.charAt(0) == ch)
				return true;
		}

		return false;
	}

	public int matchesOperator(String str, int start) {
		for (String op : getOperators()) {
			if (op.regionMatches(0, str, start, op.length()))
				return start + op.length();
		}

		return -1;
	}

	public int unescapeString(String source, int offset, StringBuffer sb)
			throws SQLParserException {
		int i = offset + 1;
		char ch;

		while (i < source.length()) {
			ch = source.charAt(i);
			switch (ch) {
			case '\\':
				sb.append(ch);
				return ++i;
			case 'n':
				sb.append('\n');
				return ++i;
			case 'r':
				sb.append('\r');
				return ++i;
			case 't':
				sb.append('\t');
				return ++i;
			case 'b':
				sb.append('\b');
				return ++i;
			case 'f':
				sb.append('\f');
				return ++i;
			case '\'':
				sb.append('\'');
				return ++i;
			case '"':
				sb.append('"');
				return ++i;

			case 'x':
				if (++i >= source.length())
					throw new SQLParserException();

				return parseHexNumber(source, i, sb, 2);

			case 'u':
				if (++i >= source.length())
					throw new SQLParserException();

				return parseHexNumber(source, i, sb, 4);

			default:
				return parseOctalNumber(source, i, sb, 3);

			}
		}

		throw new SQLParserException();
	}

	private int parseOctalNumber(String source, int offset, StringBuffer sb,
			int octalLen) throws SQLParserException {
		char ch;
		int i, v = 0;

		for (i = offset; i < offset + octalLen && i < source.length(); i++) {
			ch = source.charAt(i);
			if (!isOctalNumber(ch))
				throw new SQLParserException();

			v = v * 8 + getOctalValue(ch);
		}

		if (i != offset + octalLen)
			throw new SQLParserException();

		sb.append((char) v);

		return i;
	}

	private int parseHexNumber(String source, int offset, StringBuffer sb,
			int hexLen) throws SQLParserException {
		char ch;
		int i, v = 0;

		for (i = offset; i < offset + hexLen && i < source.length(); i++) {
			ch = source.charAt(i);
			if (!isHexNumber(ch))
				throw new SQLParserException();

			v = v * 16 + getHexValue(ch);
		}

		if (i != offset + hexLen)
			throw new SQLParserException();

		sb.append((char) v);

		return i;
	}

	public int getOctalValue(char ch) {
		assert isOctalNumber(ch);
		return ch - '0';
	}

	public int getHexValue(char ch) {
		assert isHexNumber(ch);

		if (ch >= '0' && ch <= '9') {
			return ch - '0';
		} else if (ch >= 'a' && ch <= 'f') {
			return ch - 'a' + 10;
		} else {
			return ch - 'A' + 10;
		}
	}

	/**
	 * Give subclass a chance to override this method for custom delimiters.
	 * 
	 * @return
	 */
	public String[] getDelimiters() {
		return DELIMITERS;
	}

	/**
	 * Give subclass a chance to override this method for custom operators.
	 * 
	 * @return
	 */
	public String[] getOperators() {
		return OPERATORS;
	}

}
