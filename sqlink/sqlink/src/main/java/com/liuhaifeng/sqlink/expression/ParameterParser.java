package com.liuhaifeng.sqlink.expression;

import java.util.ArrayList;
import java.util.List;

import com.liuhaifeng.sqlink.beans.Expression;
import com.liuhaifeng.sqlink.config.Keywords;
import com.liuhaifeng.sqlink.config.WordDef;
import com.liuhaifeng.sqlink.error.ErrorCode;
import com.liuhaifeng.sqlink.error.SQLCompilerException;
import com.liuhaifeng.sqlink.word.Word;

/**
 * Parameter Parser
 * 
 * @author LiuHaifeng <br>
 *         $Id: ParameterParser.java 62 2009-12-22 08:01:00Z LiuHaifeng $
 */
public class ParameterParser {

	/**
	 * Parse parameter list from the specified position of the given word list.
	 * 
	 * @param wordList
	 * @param index
	 * @return
	 * @throws SQLCompilerException
	 */
	public static ParseParameterResult parse(List<Word> wordList, int index)
			throws SQLCompilerException {
		// parse parameter
		List<Expression> parameterList = new ArrayList<Expression>();
		Word w = null;
		while (index < wordList.size()) {
			w = wordList.get(index);
			switch (w.getType()) {
			case WordDef.DE:
				if (Keywords.COMMA.equals(w.getWord())) {
					index++;
				} else if (Keywords.PARENTHESIS_END.equals(w.getWord())) {
					return new ParseParameterResult(index, parameterList);
				} else {
					throw new SQLCompilerException(w, ErrorCode.ERROR,
							ErrorCode.CG_UNEXPECTED_WORD,
							"Failed to parse parameter, expects \""
									+ Keywords.COMMA + "\" or \""
									+ Keywords.PARENTHESIS_START + "\" here.");
				}
				break;
			default:
				// parse expression
				ParseExpressionResult epRet = ExpressionParser.parse(index,
						wordList);
				if (epRet == null || epRet.getIndex() <= index) {
					throw new SQLCompilerException(w, ErrorCode.ERROR,
							ErrorCode.CG_UNEXPECTED_WORD,
							"Failed to parse parameter, unexpected word occurred.");
				}
				parameterList.add(epRet.getExpression());
				index = epRet.getIndex();
			}
		}
		throw new SQLCompilerException(w, ErrorCode.ERROR,
				ErrorCode.CG_UNEXPECTED_WORD,
				"Failed to parse parameter, unexpected word occurred.");
	}
}
