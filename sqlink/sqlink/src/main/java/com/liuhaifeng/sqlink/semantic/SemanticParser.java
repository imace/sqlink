package com.liuhaifeng.sqlink.semantic;

import java.util.List;

import com.liuhaifeng.sqlink.beans.SQL;
import com.liuhaifeng.sqlink.error.CompilerError;
import com.liuhaifeng.sqlink.error.ErrorCode;
import com.liuhaifeng.sqlink.error.SQLCompilerException;
import com.liuhaifeng.sqlink.word.Word;

/**
 * Semantic parser. Parse entities, sentences and expressions, etc.
 * 
 * @author LiuHaifeng <br>
 *         $Id: SemanticParser.java 52 2009-12-16 12:03:57Z LiuHaifeng $
 */
public class SemanticParser {

	/**
	 * Parse the given word list. The result will be put to the
	 * <b>rootDomain</b>, and the error information(if any) will be added to the
	 * <b>compilerErrorList</b>.
	 * 
	 * @param wordList
	 * @param sqlStatements
	 * @param compilerErrorList
	 * @throws SQLCompilerException
	 */
	public static void parse(List<Word> wordList, List<SQL> sqlStatements,
			List<CompilerError> compilerErrorList) throws SQLCompilerException {
		sqlStatements.clear();
		int index = 0;
		while (index < wordList.size()) {
			// parse one sentence (one single sentence or one sentence block)
			ParseSentenceResult result = SentenceParser.parse(wordList, index);
			if (result == null)
				break;

			if (result.getIndex() <= index)
				throw new SQLCompilerException(wordList.get(index),
						ErrorCode.ERROR, ErrorCode.SP_INVALID_SENTENCE,
						"Failed to parse sentence.");
			index = result.getIndex();
			sqlStatements.add(result.getSentence());
		}
	}

}
