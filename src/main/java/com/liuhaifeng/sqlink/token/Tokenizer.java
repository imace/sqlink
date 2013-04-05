package com.liuhaifeng.sqlink.token;

import java.io.Serializable;

import com.liuhaifeng.sqlink.dialect.defsql.DefaultSQLLexerStrategy;
import com.liuhaifeng.sqlink.error.SQLParserException;

/**
 * Tokenizer
 * 
 * @author Liu Haifeng
 * 
 */
public class Tokenizer implements TokenStream, Serializable {
	private static final long serialVersionUID = 1L;

	private String source;
	private int offset;
	private LexerStrategy strategy;

	private TokenType tokenType;
	private int start;
	private int end;
	private String tokenString;

	public Tokenizer(String source) {
		this(source, DefaultSQLLexerStrategy.getInstance());
	}

	public Tokenizer(String source, LexerStrategy lexStrategy) {
		this.source = source;
		this.offset = 0;
		this.strategy = lexStrategy;

		this.tokenType = null;
		this.tokenString = null;
	}

	public boolean next() throws SQLParserException {
		char ch;

		while (offset < source.length() && source.charAt(offset) == ' ')
			offset++;

		if (offset >= source.length())
			return false;

		ch = source.charAt(offset);

		if (strategy.isIdentStart(ch)) {
			return parseIdent();

		} else if (strategy.isQuoteStart(ch)) {
			return parseString(ch);

		} else if (strategy.isIdentQuoteStart(ch)) {
			if (++offset >= source.length())
				throw new SQLParserException();

			if (!strategy.isIdentStart(source.charAt(offset))) {
				throw new SQLParserException();
			}

			if (!parseIdent()) {
				throw new SQLParserException();
			}

			tokenType = TokenType.QuotedIdentifier;

			if (offset >= source.length() || ch != source.charAt(offset)) {
				throw new SQLParserException();
			}

			offset++;
			return true;

		} else if (strategy.isNumberStart(ch)) {
			if (parseNumber())
				return true;

			// should only be '.' here
			if (strategy.isDelimiterStart(ch)) {
				return parseDelimiter();

			} else if (strategy.isOperatorStart(ch)) {
				return parseOperator();
			}

		} else if (strategy.isDelimiterStart(ch)) {
			return parseDelimiter();

		} else if (strategy.isOperatorStart(ch)) {
			return parseOperator();

		} else {
			throw new SQLParserException();
		}

		return false;
	}

	private boolean parseIdent() {
		int i = offset + 1;
		while (i < source.length() && strategy.isIdentChar(source.charAt(i))) {
			i++;
		}

		tokenType = TokenType.Identifier;
		start = offset;
		end = offset = i;
		tokenString = null;

		return true;
	}

	private boolean parseString(char quote) throws SQLParserException {
		int i = offset + 1;
		StringBuffer sb = new StringBuffer();

		while (i < source.length()) {
			char ch = source.charAt(i);

			if (ch == quote) {
				tokenType = TokenType.String;
				start = offset;
				end = i;
				tokenString = sb.toString();
				offset = i + 1;

				return true;

			} else if (strategy.isEscapeChar(ch)) {
				int j = strategy.unescapeString(source, i, sb);
				if (j <= i)
					throw new SQLParserException();

				i = j;

			} else {
				sb.append(ch);
				i++;
			}
		}

		throw new SQLParserException();
	}

	private boolean parseNumber() throws SQLParserException {
		char leader = source.charAt(offset);
		boolean isFloat = (leader == '.');
		int i = offset + 1;

		if (i >= source.length()) {
			if (isFloat)
				return false;

			tokenType = TokenType.Number;
			start = offset;
			end = offset = i;
			tokenString = null;

			return true;
		}

		char ch = source.charAt(i);
		if (leader == '0') {
			if (strategy.isOctalNumber(ch)) {
				return parseOctalNumber();
			} else if (ch == 'x') {
				return parseHexNumber();
			}

		} else if (leader == '.' && !strategy.isDecNumber(ch)) {
			return false;
		}

		while (i < source.length()) {
			ch = source.charAt(i);

			if (ch == '.') {
				if (isFloat)
					break;
				else
					isFloat = true;

			} else if (!strategy.isDecNumber(ch)) {
				break;
			}

			i++;
		}

		tokenType = TokenType.Number;
		start = offset;
		end = offset = i;
		tokenString = null;

		return true;
	}

	private boolean parseOctalNumber() throws SQLParserException {
		int i = offset + 1;
		while (i < source.length() && strategy.isOctalNumber(source.charAt(i)))
			i++;

		if (i == offset + 1)
			throw new SQLParserException();

		tokenType = TokenType.Number;
		start = offset;
		end = offset = i;
		tokenString = null;

		return true;
	}

	private boolean parseHexNumber() throws SQLParserException {
		int i = offset + 2;
		while (i < source.length() && strategy.isHexNumber(source.charAt(i)))
			i++;

		if (i == offset + 2)
			throw new SQLParserException();

		tokenType = TokenType.Number;
		start = offset;
		end = offset = i;
		tokenString = null;

		return true;
	}

	private boolean parseDelimiter() {
		int i = strategy.matchesDelimiter(source, offset);

		if (i <= offset)
			return false;

		tokenType = TokenType.Delimiter;
		start = offset;
		end = offset = i;
		tokenString = null;

		return true;
	}

	private boolean parseOperator() {
		int i = strategy.matchesOperator(source, offset);

		if (i <= offset)
			return false;

		tokenType = TokenType.Operator;
		start = offset;
		end = offset = i;
		tokenString = null;

		return true;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public String getString() {
		if (tokenString == null && tokenType != null) {
			tokenString = source.substring(start, end);
		}
		return tokenString;
	}

	public boolean matchesToken(String token) {
		if (tokenString == null) {
			return source.regionMatches(false, start, token, 0, end - start);
		} else {
			return tokenString.equals(token);
		}
	}

	public boolean matchesTokenIgnoreCase(String token) {
		if (tokenString == null) {
			return source.regionMatches(true, start, token, 0, end - start);
		} else {
			return tokenString.equalsIgnoreCase(token);
		}
	}

	public Number getNumber() {
		return null;
	}

	public static void main(String[] args) throws SQLParserException {
		Tokenizer tokenizer = new Tokenizer(
				"hello world\"你好中国\\n\\tabc=\\\"\\u0024\\\"\"`abc_0`.`_this1`12.34 .35 0123 0x234 0 1.1.1 087 0x9fFEgH+++,++,(-)");
		while (tokenizer.next()) {
			System.out.println(tokenizer.getTokenType() + ": "
					+ tokenizer.getString());
		}
	}
}
