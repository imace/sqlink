package com.liuhaifeng.sqlink.word;

import java.util.List;

import com.liuhaifeng.sqlink.config.WordDef;
import com.liuhaifeng.sqlink.error.CompilerError;
import com.liuhaifeng.sqlink.error.ErrorCode;

/**
 * Symbol parser.
 * 
 * @author LiuHaifeng <br>
 *         $Id: SymbolParser.java 46 2009-12-04 14:28:03Z LiuHaifeng $
 */
public class SymbolParser {

	/**
	 * Parse symbol string, split the string to symbols.
	 * 
	 * @param line
	 * @param column
	 * @param symbols
	 * @param wordList
	 * @param compilerErrorList
	 */
	public static void parse(int line, int column, String symbols,
			List<Word> wordList, List<CompilerError> compilerErrorList) {
		// maximum matching, delimiters first.
		int colIndex = 0;
		int arrIndex = 0;
		while (colIndex < symbols.length()) {
			for (arrIndex = 0; arrIndex < WordDef.delimiters.length; arrIndex++) {
				if (symbols.startsWith(WordDef.delimiters[arrIndex], colIndex)) {
					// got one
					WordParser.addWord(WordDef.DE, line, column + colIndex,
							symbols.substring(colIndex, colIndex
									+ WordDef.delimiters[arrIndex].length()),
							wordList);
					colIndex += WordDef.delimiters[arrIndex].length();
					break;
				}
			}
			if (arrIndex >= WordDef.delimiters.length) {
				// no matched delimiter, try to match operators
				for (arrIndex = 0; arrIndex < WordDef.operators.length; arrIndex++) {
					if (symbols.startsWith(WordDef.operators[arrIndex],
							colIndex)) {
						// got one
						WordParser
								.addWord(WordDef.OP, line, column + colIndex,
										symbols.substring(colIndex, colIndex
												+ WordDef.operators[arrIndex]
														.length()), wordList);
						colIndex += WordDef.operators[arrIndex].length();
						break;
					}
				}
				if (arrIndex >= WordDef.operators.length) {
					// no matched delimiter, no matched operator, unknown
					// symbol.
					WordParser.addError(line, column + colIndex,
							ErrorCode.ERROR, ErrorCode.WP_INVALID_SYMBOL,
							"Unrecognized symbol.", compilerErrorList);
					colIndex++;
				}
			}
		}
	}
}
