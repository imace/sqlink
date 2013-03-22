package com.liuhaifeng.sqlink.semantic;

import java.util.List;

import com.liuhaifeng.sqlink.error.SQLCompilerException;
import com.liuhaifeng.sqlink.word.Word;

public class SearchParser {

	/**
	 * Parse <b>SEARCH</b> statement from the specified position of the given
	 * word list.
	 * 
	 * @param index
	 * @param wordList
	 * @return
	 * @throws SQLCompilerException
	 */
	public static ParseSentenceResult parse(int index, List<Word> wordList)
			throws SQLCompilerException {
		return new ParseSentenceResult(index, null);
	}

}
