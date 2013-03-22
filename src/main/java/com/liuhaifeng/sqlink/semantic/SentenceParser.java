package com.liuhaifeng.sqlink.semantic;

import java.util.List;

import com.liuhaifeng.sqlink.config.Keywords;
import com.liuhaifeng.sqlink.config.WordDef;
import com.liuhaifeng.sqlink.error.ErrorCode;
import com.liuhaifeng.sqlink.error.SQLCompilerException;
import com.liuhaifeng.sqlink.word.Word;

/**
 * Sentence Parser.
 * 
 * @author LiuHaifeng <br>
 *         $Id: SentenceParser.java 58 2009-12-18 08:11:50Z LiuHaifeng $
 */
public class SentenceParser {

	/**
	 * Parse sentences from the specified position of the given word list.
	 * 
	 * @param wordList
	 * @param index
	 * @return
	 * @throws SQLCompilerException
	 */
	public static ParseSentenceResult parse(List<Word> wordList, int index)
			throws SQLCompilerException {
		Word w = wordList.get(index);
		switch (w.getType()) {
		case WordDef.DE:
			if (Keywords.SENTENCE_DELIMITER.equals(w.getWord())) {
				return new ParseSentenceResult(++index, null);
			} else {
				return null;
			}
		case WordDef.KEYWORD:
			if (Keywords.SELECT.equalsIgnoreCase(w.getWord())) {
				// parse select sentence
				ParseSentenceResult result = SelectParser
						.parse(index, wordList);
				if (result == null) {
					throw new SQLCompilerException(w, ErrorCode.ERROR,
							ErrorCode.SP_INVALID_SENTENCE,
							"Invalid \"select\" sentence.");
				} else {
					return result;
				}
			} else if (Keywords.UPDATE.equalsIgnoreCase(w.getWord())) {
				// parse update
				ParseSentenceResult result = UpdateParser
						.parse(index, wordList);
				if (result == null) {
					throw new SQLCompilerException(w, ErrorCode.ERROR,
							ErrorCode.SP_INVALID_SENTENCE,
							"Invalid \"update\" sentence.");
				} else {
					return result;
				}
			} else if (Keywords.INSERT.equalsIgnoreCase(w.getWord())) {
				// parse insert
				ParseSentenceResult result = InsertParser
						.parse(index, wordList);
				if (result == null) {
					throw new SQLCompilerException(w, ErrorCode.ERROR,
							ErrorCode.SP_INVALID_SENTENCE,
							"Invalid \"insert\" sentence.");
				} else {
					return result;
				}
			} else if (Keywords.DELETE.equalsIgnoreCase(w.getWord())) {
				// parse delete
				ParseSentenceResult result = DeleteParser
						.parse(index, wordList);
				if (result == null) {
					throw new SQLCompilerException(w, ErrorCode.ERROR,
							ErrorCode.SP_INVALID_SENTENCE,
							"Invalid \"delete\" sentence.");
				} else {
					return result;
				}
			} else if (Keywords.SEARCH.equalsIgnoreCase(w.getWord())) {
				// parse delete
				ParseSentenceResult result = SearchParser
						.parse(index, wordList);
				if (result == null) {
					throw new SQLCompilerException(w, ErrorCode.ERROR,
							ErrorCode.SP_INVALID_SENTENCE,
							"Invalid \"search\" sentence.");
				} else {
					return result;
				}
			} else {
				throw new SQLCompilerException(w, ErrorCode.ERROR,
						ErrorCode.CG_UNEXPECTED_WORD,
						"Unexpected identification occurred.");
			}
		default:
			return null;
		}

	}

}
