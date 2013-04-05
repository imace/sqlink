package com.liuhaifeng.sqlink.token;

import com.liuhaifeng.sqlink.error.SQLParserException;

public interface TokenStream {

	public enum TokenType {
		Identifier, QuotedIdentifier, String, Number, Operator, Delimiter
	}

	boolean next() throws SQLParserException;

	TokenType getTokenType();

	String getString();

	boolean matchesToken(String token);

	boolean matchesTokenIgnoreCase(String token);

	Number getNumber();

}
