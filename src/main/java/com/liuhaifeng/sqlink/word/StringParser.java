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
 *         $Id: StringParser.java 46 2009-12-04 14:28:03Z LiuHaifeng $
 */
public class StringParser {
	public static final int NORMAL = 0;
	public static final int TM = 1; // transfered meaning
	public static final int OCT_0 = 2; // look for octal number (3_1)
	public static final int OCT_1 = 3; // look for octal number (3_2)
	public static final int OCT_2 = 4; // look for octal number (3_3)
	public static final int HEX_0 = 5; // look for hex number (4_1)
	public static final int HEX_1 = 6; // look for hex number (4_2)
	public static final int HEX_2 = 7; // look for hex number (4_3)
	public static final int HEX_3 = 8; // look for hex number (4_4)
	public static final int END = 1000;

	public static final String[] tmList = { "\n", "\r", "\t", "\b", "\f",
			"\001", "\"", "\'" };

	/**
	 * Parse string constant.
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
		int state = NORMAL;
		char ch; // current char.
		int index = start; // used to looking for hex or octal number.

		for (int i = start; i < lineStr.length(); i++) {
			ch = lineStr.charAt(i);
			switch (state) {
			case NORMAL:
				if (ch == '\\') {
					state = TM;
				} else if (ch == quote) {
					WordParser.addWord(WordDef.ST, line, start + 1, sb
							.toString(), wordList);
					return i;
				} else {
					sb.append(ch);
				}
				break;
			case TM:
				switch (ch) {
				case 'n':
					sb.append('\n');
					state = NORMAL;
					break;
				case 'r':
					sb.append('\r');
					state = NORMAL;
					break;
				case 't':
					sb.append('\t');
					state = NORMAL;
					break;
				case 'b':
					sb.append('\b');
					state = NORMAL;
					break;
				case 'f':
					sb.append('\f');
					state = NORMAL;
					break;
				case '\'':
					sb.append('\'');
					state = NORMAL;
					break;
				case '"':
					sb.append('"');
					state = NORMAL;
					break;
				case '\\':
					sb.append('\\');
					state = NORMAL;
					break;
				case 'u':
					state = HEX_0;
					break;
				default:
					if (isOctalChar(ch)) {
						state = OCT_1;
						index = i;
					} else {
						throw new SQLCompilerException(line, i + 1,
								ErrorCode.ERROR, ErrorCode.WP_INVALID_STRING,
								"Unrecognized transfered meaing.");
					}
				}
				break;
			case HEX_0:
				index = i;
				if (isHexChar(ch)) {
					state = HEX_1;
				} else {
					throw new SQLCompilerException(line, i + 1,
							ErrorCode.ERROR, ErrorCode.WP_INVALID_STRING,
							"Unrecognized transfered meaing.");
				}
				break;
			case HEX_1:
				if (isHexChar(ch)) {
					state = HEX_2;
				} else {
					throw new SQLCompilerException(line, i + 1,
							ErrorCode.ERROR, ErrorCode.WP_INVALID_STRING,
							"Unrecognized transfered meaing.");
				}
				break;
			case HEX_2:
				if (isHexChar(ch)) {
					state = HEX_3;
				} else {
					throw new SQLCompilerException(line, i + 1,
							ErrorCode.ERROR, ErrorCode.WP_INVALID_STRING,
							"Unrecognized transfered meaing.");
				}
				break;
			case HEX_3:
				if (isHexChar(ch)) {
					sb.append(getCharByHexString(lineStr
							.substring(index, i + 1)));
					state = NORMAL;
				} else {
					throw new SQLCompilerException(line, i + 1,
							ErrorCode.ERROR, ErrorCode.WP_INVALID_STRING,
							"Unrecognized transfered meaing.");
				}
				break;
			case OCT_0:
				index = i;
				if (isOctalChar(ch)) {
					state = OCT_1;
				} else {
					throw new SQLCompilerException(line, i + 1,
							ErrorCode.ERROR, ErrorCode.WP_INVALID_STRING,
							"Unrecognized transfered meaing.");
				}
				break;
			case OCT_1:
				if (isOctalChar(ch)) {
					state = OCT_2;
				} else {
					sb
							.append(getCharByOctalString(lineStr.substring(
									index, i)));
					if (ch == '\\') {
						state = TM;
					} else {
						sb.append(ch);
						state = NORMAL;
					}
				}
				break;
			case OCT_2:
				if (isOctalChar(ch)) {
					sb.append(getCharByOctalString(lineStr.substring(index,
							i + 1)));
					state = NORMAL;
				} else {
					sb
							.append(getCharByOctalString(lineStr.substring(
									index, i)));
					if (ch == '\\') {
						state = TM;
					} else {
						sb.append(ch);
						state = NORMAL;
					}
				}
				break;
			}
		}
		throw new SQLCompilerException(line, start + 1, ErrorCode.ERROR,
				ErrorCode.WP_INVALID_STRING, "Unterminated constant string.");
	}

	/**
	 * Judge that if the given char is a hex number.
	 * 
	 * @param ch
	 * @return
	 */
	private static boolean isHexChar(char ch) {
		return ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F'));
	}

	/**
	 * Judge that if the given char is octal number.
	 * 
	 * @param ch
	 * @return
	 */
	private static boolean isOctalChar(char ch) {
		return (ch >= '0' && ch <= '7');
	}

	/**
	 * Get the char given in hex format.
	 * 
	 * @param hexStr
	 * @return
	 */
	private static char getCharByHexString(String hexStr) {
		return (char) Integer.parseInt(hexStr, 16);
	}

	/**
	 * Get the char given in octal format.
	 * 
	 * @param octStr
	 * @return
	 */
	private static char getCharByOctalString(String octStr) {
		return (char) Integer.parseInt(octStr, 8);
	}
}
