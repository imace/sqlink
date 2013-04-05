package com.liuhaifeng.sqlink.dialect.defsql;

import junit.framework.TestCase;

import com.liuhaifeng.sqlink.dialect.defsql.DefaultSQLLexerStrategy;
import com.liuhaifeng.sqlink.error.SQLParserException;

public class TestDefaultSQLLexerStrategy extends TestCase {

	private DefaultSQLLexerStrategy strategy = new DefaultSQLLexerStrategy();

	public void testIdentStart() {
		assertTrue(strategy.isIdentStart('a'));
		assertTrue(strategy.isIdentStart('c'));
		assertTrue(strategy.isIdentStart('z'));
		assertTrue(strategy.isIdentStart('A'));
		assertTrue(strategy.isIdentStart('F'));
		assertTrue(strategy.isIdentStart('Z'));
		assertTrue(strategy.isIdentStart('_'));

		assertFalse(strategy.isIdentStart('0'));
		assertFalse(strategy.isIdentStart('9'));
		assertFalse(strategy.isIdentStart('-'));
		assertFalse(strategy.isIdentStart('*'));
	}

	public void testIdentChar() {
		assertTrue(strategy.isIdentChar('a'));
		assertTrue(strategy.isIdentChar('c'));
		assertTrue(strategy.isIdentChar('z'));
		assertTrue(strategy.isIdentChar('A'));
		assertTrue(strategy.isIdentChar('F'));
		assertTrue(strategy.isIdentChar('Z'));
		assertTrue(strategy.isIdentChar('_'));
		assertTrue(strategy.isIdentChar('0'));
		assertTrue(strategy.isIdentChar('9'));

		assertFalse(strategy.isIdentChar('-'));
		assertFalse(strategy.isIdentChar('*'));
	}

	public void testNumberStart() {
		assertTrue(strategy.isNumberStart('0'));
		assertTrue(strategy.isNumberStart('1'));
		assertTrue(strategy.isNumberStart('2'));
		assertTrue(strategy.isNumberStart('3'));
		assertTrue(strategy.isNumberStart('4'));
		assertTrue(strategy.isNumberStart('5'));
		assertTrue(strategy.isNumberStart('6'));
		assertTrue(strategy.isNumberStart('7'));
		assertTrue(strategy.isNumberStart('8'));
		assertTrue(strategy.isNumberStart('9'));
		assertTrue(strategy.isNumberStart('.'));

		assertFalse(strategy.isNumberStart('a'));
		assertFalse(strategy.isNumberStart('f'));
		assertFalse(strategy.isNumberStart('z'));
		assertFalse(strategy.isNumberStart('A'));
		assertFalse(strategy.isNumberStart('F'));
		assertFalse(strategy.isNumberStart('Z'));
		assertFalse(strategy.isNumberStart('-'));
		assertFalse(strategy.isNumberStart('*'));
	}

	public void testOctalNumber() {
		assertTrue(strategy.isOctalNumber('0'));
		assertTrue(strategy.isOctalNumber('1'));
		assertTrue(strategy.isOctalNumber('2'));
		assertTrue(strategy.isOctalNumber('3'));
		assertTrue(strategy.isOctalNumber('4'));
		assertTrue(strategy.isOctalNumber('5'));
		assertTrue(strategy.isOctalNumber('6'));
		assertTrue(strategy.isOctalNumber('7'));

		assertFalse(strategy.isOctalNumber('8'));
		assertFalse(strategy.isOctalNumber('9'));
		assertFalse(strategy.isOctalNumber('a'));
		assertFalse(strategy.isOctalNumber('f'));
		assertFalse(strategy.isOctalNumber('z'));
		assertFalse(strategy.isOctalNumber('A'));
		assertFalse(strategy.isOctalNumber('F'));
		assertFalse(strategy.isOctalNumber('Z'));
		assertFalse(strategy.isOctalNumber('-'));
		assertFalse(strategy.isOctalNumber('*'));
	}

	public void testDecNumber() {
		assertTrue(strategy.isDecNumber('0'));
		assertTrue(strategy.isDecNumber('1'));
		assertTrue(strategy.isDecNumber('2'));
		assertTrue(strategy.isDecNumber('3'));
		assertTrue(strategy.isDecNumber('4'));
		assertTrue(strategy.isDecNumber('5'));
		assertTrue(strategy.isDecNumber('6'));
		assertTrue(strategy.isDecNumber('7'));
		assertTrue(strategy.isDecNumber('8'));
		assertTrue(strategy.isDecNumber('9'));

		assertFalse(strategy.isDecNumber('a'));
		assertFalse(strategy.isDecNumber('f'));
		assertFalse(strategy.isDecNumber('z'));
		assertFalse(strategy.isDecNumber('A'));
		assertFalse(strategy.isDecNumber('F'));
		assertFalse(strategy.isDecNumber('Z'));
		assertFalse(strategy.isDecNumber('-'));
		assertFalse(strategy.isDecNumber('*'));
	}

	public void testHexNumber() {
		assertTrue(strategy.isHexNumber('0'));
		assertTrue(strategy.isHexNumber('1'));
		assertTrue(strategy.isHexNumber('2'));
		assertTrue(strategy.isHexNumber('3'));
		assertTrue(strategy.isHexNumber('4'));
		assertTrue(strategy.isHexNumber('5'));
		assertTrue(strategy.isHexNumber('6'));
		assertTrue(strategy.isHexNumber('7'));
		assertTrue(strategy.isHexNumber('8'));
		assertTrue(strategy.isHexNumber('9'));
		assertTrue(strategy.isHexNumber('a'));
		assertTrue(strategy.isHexNumber('f'));
		assertTrue(strategy.isHexNumber('A'));
		assertTrue(strategy.isHexNumber('F'));

		assertFalse(strategy.isHexNumber('z'));
		assertFalse(strategy.isHexNumber('Z'));
		assertFalse(strategy.isHexNumber('-'));
		assertFalse(strategy.isHexNumber('*'));
	}

	public void testEscape() throws SQLParserException {
		assertTrue(strategy.isEscapeChar('\\'));
		assertFalse(strategy.isEscapeChar('/'));
		assertFalse(strategy.isEscapeChar('a'));

		StringBuffer sb = new StringBuffer();
		strategy.unescapeString("\\044", 0, sb);
		assertEquals("$", sb.toString());
		sb.delete(0, sb.length());

		strategy.unescapeString("\\x24", 0, sb);
		assertEquals("$", sb.toString());
		sb.delete(0, sb.length());

		strategy.unescapeString("\\u0024", 0, sb);
		assertEquals("$", sb.toString());
		sb.delete(0, sb.length());

		strategy.unescapeString("\\n", 0, sb);
		assertEquals("\n", sb.toString());
		sb.delete(0, sb.length());

		strategy.unescapeString("\\r", 0, sb);
		assertEquals("\r", sb.toString());
		sb.delete(0, sb.length());

		strategy.unescapeString("\\t", 0, sb);
		assertEquals("\t", sb.toString());
		sb.delete(0, sb.length());

		strategy.unescapeString("\\f", 0, sb);
		assertEquals("\f", sb.toString());
		sb.delete(0, sb.length());

		strategy.unescapeString("\\b", 0, sb);
		assertEquals("\b", sb.toString());
		sb.delete(0, sb.length());
	}

	public void testMisc() {
		assertEquals(1, strategy.matchesDelimiter("(a", 0));
		assertEquals(2, strategy.matchesDelimiter("a(", 1));
		assertEquals(1, strategy.matchesOperator("+", 0));
		assertEquals(2, strategy.matchesOperator("++", 0));
		assertEquals(2, strategy.matchesOperator("a+", 1));
		assertEquals(3, strategy.matchesOperator("a++", 1));
	}
}
