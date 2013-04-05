package com.liuhaifeng.sqlink;

import junit.framework.TestCase;

import com.liuhaifeng.sqlink.error.SQLParserException;
import com.liuhaifeng.sqlink.token.TokenStream.TokenType;
import com.liuhaifeng.sqlink.token.Tokenizer;

public class TestTokenizer extends TestCase {

	private void assertOneToken(String source, TokenType expectedType,
			String expectedString) throws SQLParserException {
		Tokenizer tokenizer = new Tokenizer(source);
		assertTrue(tokenizer.next());
		assertEquals(expectedType, tokenizer.getTokenType());
		assertEquals(expectedString, tokenizer.getString());
	}

	public void testNumber() throws SQLParserException {
		assertOneToken("0", TokenType.Number, "0");
		assertOneToken("1", TokenType.Number, "1");
		assertOneToken("9", TokenType.Number, "9");
		assertOneToken("0.0", TokenType.Number, "0.0");
		assertOneToken("0.5", TokenType.Number, "0.5");
		assertOneToken("0.14159", TokenType.Number, "0.14159");
		assertOneToken(".0", TokenType.Number, ".0");
		assertOneToken(".14159", TokenType.Number, ".14159");
	}

	/**
	 * 
	 * @throws SQLParserException
	 * 
	 */
	public static void main(String[] args) throws SQLParserException {
		String sql = "select `a`.`arc_id`, `a`.`arc_title` from ph_archives "
				+ "where `arc_rank` >= 50 and arc_status = 'OPEN' or arc_author is null or arc_source != 'http://' "
				+ "and arc_name <> 'test' or arc_rank <= 10 order by `arc_created` desc limit 0, 8";
		long ts = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			Tokenizer tokenizer = new Tokenizer(sql);
			while (tokenizer.next()) {
				// System.out.println(tokenizer.getTokenType() + "\t=> "
				// + tokenizer.getString());
			}
		}
		long cost = System.currentTimeMillis() - ts;
		System.out.println("Cost: " + cost);
	}

}
