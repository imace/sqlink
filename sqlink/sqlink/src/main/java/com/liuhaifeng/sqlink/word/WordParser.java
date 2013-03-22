package com.liuhaifeng.sqlink.word;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import com.liuhaifeng.sqlink.config.Keywords;
import com.liuhaifeng.sqlink.config.WordDef;
import com.liuhaifeng.sqlink.error.CompilerError;
import com.liuhaifeng.sqlink.error.ErrorCode;
import com.liuhaifeng.sqlink.error.SQLCompilerException;

/**
 * Word parser.
 * 
 * @author LiuHaifeng <br>
 *         $Id: WordParser.java 67 2009-12-29 12:29:10Z LiuHaifeng $
 */
public class WordParser {

	/**
	 * Parse SQL words.
	 * 
	 * @param sqlStr
	 * @param wordList
	 * @param compilerErrorList
	 * @throws IOException
	 */
	public static void parse(String sqlStr, List<Word> wordList,
			List<CompilerError> compilerErrorList) throws IOException {
		BufferedReader br = new BufferedReader(new StringReader(sqlStr));
		String lineStr = null;
		int lineNum = 0;
		int wordState = WordDef.BE;
		while ((lineStr = br.readLine()) != null) {
			lineNum++;

			if (wordState == WordDef.EN)
				wordState = WordDef.BE;

			try {
				wordState = parseLine(wordState, lineStr, lineNum, wordList,
						compilerErrorList);
			} catch (SQLCompilerException e) {
				addError(e, compilerErrorList);
			}
		}
		if (wordState != WordDef.EN)
			addError(
					lineNum,
					0,
					ErrorCode.ERROR,
					ErrorCode.WP_INVALID_SOURCE,
					"Bad end, probably something is not completed or in wrong format.",
					compilerErrorList);
		// post parse word.
		postParseWord(sqlStr, wordList, compilerErrorList);
	}

	/**
	 * Parse one line of SQL source code.
	 * 
	 * @param startState
	 * @param lineStr
	 * @param lineNum
	 * @param wordList
	 * @param compilerErrorList
	 * @return
	 * @throws SQLCompilerException
	 */
	public static int parseLine(int startState, String lineStr, int lineNum,
			List<Word> wordList, List<CompilerError> compilerErrorList)
			throws SQLCompilerException {
		int colIndex = 0;
		int oldState = startState;
		int state = startState;

		// @Debug
		// System.out.println("\r\n==> Line " + lineNum + ": " + lineStr);

		for (int i = 0; i < lineStr.length(); i++) {
			oldState = state;
			if (oldState == WordDef.ST) {
				i = StringParser.parse(lineNum, colIndex, lineStr, wordList,
						compilerErrorList);
				state = WordDef.BE;
			} else if (oldState == WordDef.RF) {
				i = ReferStringParser.parse(lineNum, colIndex, lineStr,
						wordList, compilerErrorList);
				state = WordDef.BE;
			} else {
				state = WordDef.transition[WordDef.getAction(lineStr.charAt(i))][oldState];
				if (state == WordDef.UN) // met unknown char.
					compilerErrorList.add(new CompilerError(lineNum, i + 1,
							ErrorCode.ERROR, ErrorCode.CG_UNEXPECTED_WORD,
							"Met unexpected char \'" + lineStr.charAt(i)
									+ "\'."));
			}

			colIndex = onStateChange(oldState, state, lineStr, lineNum,
					colIndex, i, wordList, compilerErrorList);

			if (state == WordDef.EN)
				break;
		}
		if (state != WordDef.EN && state != WordDef.BC && state != WordDef.CE) {
			// terminate
			oldState = state;
			state = WordDef.EN;

			colIndex = onStateChange(oldState, state, lineStr, lineNum,
					colIndex, lineStr.length(), wordList, compilerErrorList);
		}
		return state;
	}

	/**
	 * This method will be invoked after the state changed. It try to get new
	 * word, or ignore it.
	 * 
	 * @param oldState
	 * @param newState
	 * @param lineStr
	 * @param lineNum
	 * @param start
	 * @param end
	 * @param wordList
	 * @param compilerErrorList
	 * @return
	 */
	private static int onStateChange(int oldState, int newState,
			String lineStr, int lineNum, int start, int end,
			List<Word> wordList, List<CompilerError> compilerErrorList) {

		// @Debug
		// if (newState != oldState)
		// System.out.println(end == lineStr.length() ? "\\0" : (lineStr
		// .charAt(end))
		// + ": "
		// + WordDef.getStateName(oldState)
		// + " ==> "
		// + WordDef.getStateName(newState));

		if (newState == oldState)
			return start; // no change
		if (oldState == WordDef.ST || oldState == WordDef.RF)
			return start; // string is processed by sub-FSM.
		if (newState == WordDef.DO
				&& (oldState == WordDef.DC || oldState == WordDef.IN))
			return start; // integer or dot change to double, skip.
		if (oldState == WordDef.LO || oldState == WordDef.FL)
			return ++start; // got "f" or "l" after numeric.
		if (oldState != WordDef.CS
				&& (newState == WordDef.LC || newState == WordDef.BC))
			return start; // comment
		if (oldState == WordDef.CS && newState == WordDef.SY)
			return start; // still symbol.
		if (oldState == WordDef.SY && newState == WordDef.CS)
			return start; // may be still symbol, if not, split it at last.

		if (newState == WordDef.LO || newState == WordDef.FL) {
			preAddWord(newState, lineNum, start + 1, lineStr.substring(start,
					end), wordList, compilerErrorList);
		} else if (oldState != WordDef.BE && oldState != WordDef.LC
				&& oldState != WordDef.BC && oldState != WordDef.CE) {
			if (newState == WordDef.LC || newState == WordDef.BC)
				end--; // "end" is the 2nd comment symbol
			preAddWord(oldState, lineNum, start + 1, lineStr.substring(start,
					end), wordList, compilerErrorList);
		}
		start = end;
		if (newState == WordDef.ST || newState == WordDef.RF)
			start++; // across the quote.
		return start;
	}

	/**
	 * Pre-add word to the word list. This method mainly set or correct some
	 * word information if it has to do.
	 * 
	 * @param type
	 * @param line
	 * @param column
	 * @param word
	 * @param wordList
	 * @param compilerErrorList
	 */
	public static void preAddWord(int type, int line, int column, String word,
			List<Word> wordList, List<CompilerError> compilerErrorList) {
		if (type == WordDef.DC || type == WordDef.CS)
			type = WordDef.SY;

		if (type == WordDef.SY) {
			SymbolParser.parse(line, column, word, wordList, compilerErrorList);
		} else if (type == WordDef.ID) {
			if (Keywords.NULL.equalsIgnoreCase(word))
				type = WordDef.NU;
			else if (Keywords.TRUE.equalsIgnoreCase(word)
					|| Keywords.FALSE.equalsIgnoreCase(word)) {
				type = WordDef.BO;
			} else {
				// if the ident is a keyword
				for (String kw : Keywords.keywords) {
					if (kw.equalsIgnoreCase(word)) {
						type = WordDef.KEYWORD;
						// if the keyword is an operator, or the alias of an
						// operator
						for (String opr : Keywords.operators) {
							// skip the special operator "#"
							if (opr.equals("#"))
								continue;
							if (opr.equalsIgnoreCase(word)) {
								type = WordDef.OP;
								break;
							}
						}
						if (type == WordDef.KEYWORD
								&& Keywords.operatorAliasMap.get(word
										.toLowerCase()) != null) {
							type = WordDef.OP;
						}
						break;
					}
				}
			}
			addWord(type, line, column, word, wordList);
		} else {
			addWord(type, line, column, word, wordList);
		}
	}

	/**
	 * Add word to the word list.
	 * 
	 * @param type
	 * @param line
	 * @param column
	 * @param word
	 * @param wordList
	 */
	public static void addWord(int type, int line, int column, String word,
			List<Word> wordList) {
		wordList.add(new Word(line, column, type, word));

		// @Debug
		// System.out.println(word + "\t" + column + "\t"
		// + WordDefine.getStateName(type) + "(" + type + ")");
		// System.out.print(" " + word);

	}

	/**
	 * Post parse word
	 * 
	 * @param sqlStr
	 * @param wordList
	 * @param compilerErrorList
	 */
	private static void postParseWord(String sqlStr, List<Word> wordList,
			List<CompilerError> compilerErrorList) {
		for (int i = 0; i < wordList.size(); i++) {
			Word w = wordList.get(i);
			switch (w.getType()) {
			case WordDef.ID:
				break;
			case WordDef.OP:
				if ("*".equals(w.getWord())) {
					if (i == 0) {
						w.setType(WordDef.RF);// should be an error.
					} else {
						Word w2 = wordList.get(i - 1);
						if (w2.getType() == WordDef.KEYWORD
								|| (w2.getType() == WordDef.OP
										&& (".".equals(w2.getWord()))
										|| Keywords.PARENTHESIS_START.equals(w2
												.getWord()) || Keywords.COMMA
										.equals(w2.getWord()))) {
							w.setType(WordDef.RF);
						}
					}
				} else if ("?".equals(w.getWord())) {
					// treat "?" as an parameter holder.
					w.setType(WordDef.HOLDER);
				}
				break;
			default:
			}
		}
	}

	/**
	 * Add an error to the compiler error list.
	 * 
	 * @param e
	 * @param compilerErrorList
	 */
	private static void addError(SQLCompilerException e,
			List<CompilerError> compilerErrorList) {
		compilerErrorList.add(e.getCompilerError());
	}

	/**
	 * Add an error to the compiler error list.
	 * 
	 * @param line
	 * @param column
	 * @param errorLevel
	 * @param errorCode
	 * @param msg
	 * @param compilerErrorList
	 */
	public static void addError(int line, int column, int errorLevel,
			int errorCode, String msg, List<CompilerError> compilerErrorList) {
		compilerErrorList.add(new CompilerError(line, column, errorLevel,
				errorCode, msg));
	}
}
