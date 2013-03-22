package com.liuhaifeng.sqlink.word;

import java.util.List;

import com.liuhaifeng.sqlink.config.WordDef;
import com.liuhaifeng.sqlink.error.CompilerError;
import com.liuhaifeng.sqlink.error.ErrorCode;
import com.liuhaifeng.sqlink.error.SQLCompilerException;

/**
 * String parser.
 * 
 * @author LiuHaifeng <br>
 *         $Id: ReferStringParser.java 50 2009-12-16 07:55:37Z LiuHaifeng $
 */
public class ReferStringParser {
	public static final int BEGIN = 0;
	public static final int NORMAL = 1;
	public static final int END = 1000;

	/**
	 * Parse refer string.
	 * 
	 * @param line
	 * @param start
	 * @param lineStr
	 * @param wordParser
	 * @return
	 * @throws SQLCompilerException
	 */
	public static int parse(int line, int start, String lineStr,
			List<Word> wordList, List<CompilerError> compilerErrorList)
			throws SQLCompilerException {

		char quote = lineStr.charAt(start - 1);
		StringBuffer sb = new StringBuffer("");
		int state = BEGIN;
		char ch; // current char.

		for (int i = start; i < lineStr.length(); i++) {
			ch = lineStr.charAt(i);
			switch (state) {
			case BEGIN:
				if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
					state = NORMAL;
					sb.append(ch);
				} else {
					throw new SQLCompilerException(line, i + 1,
							ErrorCode.ERROR, ErrorCode.WP_INVALID_STRING,
							"Invalid refer string.");
				}
				break;
			case NORMAL:
				if (ch == quote) {
					WordParser.addWord(WordDef.RF, line, start + 1, sb
							.toString(), wordList);
					return i;
				} else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
						|| (ch >= '0' && ch <= '9') || ch == '_') {
					sb.append(ch);
				} else {
					throw new SQLCompilerException(line, i + 1,
							ErrorCode.ERROR, ErrorCode.WP_INVALID_STRING,
							"Invalid refer string.");
				}
				break;
			}
		}
		throw new SQLCompilerException(line, start + 1, ErrorCode.ERROR,
				ErrorCode.WP_INVALID_STRING, "Unterminated constant string.");
	}

}
