package com.liuhaifeng.sqlink.semantic;

import java.util.List;

import com.liuhaifeng.sqlink.beans.Delete;
import com.liuhaifeng.sqlink.beans.Entity;
import com.liuhaifeng.sqlink.beans.Pagination;
import com.liuhaifeng.sqlink.beans.SQL;
import com.liuhaifeng.sqlink.config.Keywords;
import com.liuhaifeng.sqlink.config.WordDef;
import com.liuhaifeng.sqlink.error.ErrorCode;
import com.liuhaifeng.sqlink.error.SQLCompilerException;
import com.liuhaifeng.sqlink.expression.ExpressionParser;
import com.liuhaifeng.sqlink.expression.ParseExpressionResult;
import com.liuhaifeng.sqlink.word.Word;

public class DeleteParser {

	/**
	 * Parse <b>DELETE</b> statement from the specified position of the given
	 * word list.
	 * 
	 * @param index
	 * @param wordList
	 * @return
	 * @throws SQLCompilerException
	 */
	public static ParseSentenceResult parse(int index, List<Word> wordList)
			throws SQLCompilerException {
		// check "delete" keyword
		Word w = wordList.get(index);
		check((w.getType() == WordDef.KEYWORD && Keywords.DELETE
				.equalsIgnoreCase(w.getWord())), w, ErrorCode.ERROR,
				ErrorCode.CG_UNEXPECTED_WORD, "Expects keyword \""
						+ Keywords.DELETE + "\" here.");

		index++;
		check(index < wordList.size(), w, ErrorCode.ERROR,
				ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

		// check "from" keyword
		w = wordList.get(index);
		check((w.getType() == WordDef.KEYWORD && Keywords.FROM
				.equalsIgnoreCase(w.getWord())), w, ErrorCode.ERROR,
				ErrorCode.CG_UNEXPECTED_WORD, "Expects keyword \""
						+ Keywords.FROM + "\" here.");

		index++;
		check(index < wordList.size(), w, ErrorCode.ERROR,
				ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

		Delete delete = new Delete();

		// parse target.
		w = wordList.get(index);
		check((w.getType() == WordDef.ID || w.getType() == WordDef.RF), w,
				ErrorCode.ERROR, ErrorCode.CG_UNEXPECTED_WORD,
				"Only expects one valid entity name here.");
		Entity entity = new Entity();
		entity.setEntity(w.getWord());
		delete.setTarget(entity);

		index++;
		if (index >= wordList.size())
			return new ParseSentenceResult(index, new SQL(delete));

		// if has where condition
		w = wordList.get(index);
		if (w.getType() == WordDef.KEYWORD
				&& Keywords.WHERE.equalsIgnoreCase(w.getWord())) {
			index++;
			check(index < wordList.size(), w, ErrorCode.ERROR,
					ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

			// parse "where" condition
			ParseExpressionResult result = ExpressionParser.parse(index,
					wordList);
			check(result.getIndex() > index && result.getExpression() != null,
					wordList.get(index), ErrorCode.ERROR,
					ErrorCode.EP_INVALID_EXPRESSION,
					"Failed to parse \"where\" condition.");
			delete.setCondition(result.getExpression());

			index = result.getIndex();
			if (index >= wordList.size()) {
				return new ParseSentenceResult(index, new SQL(delete));
			}
		}

		// if has sort declare
		w = wordList.get(index);
		if (w.getType() == WordDef.KEYWORD
				&& Keywords.ORDER.equalsIgnoreCase(w.getWord())) {

			index++;
			check(index < wordList.size(), w, ErrorCode.ERROR,
					ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

			/* "by" */
			w = wordList.get(index);
			check((w.getType() == WordDef.KEYWORD && Keywords.BY
					.equalsIgnoreCase(w.getWord())), w, ErrorCode.ERROR,
					ErrorCode.CG_UNEXPECTED_WORD, "Only expects \""
							+ Keywords.BY + "\"");

			index++;
			check(index < wordList.size(), w, ErrorCode.ERROR,
					ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

			// parse "order" list
			int iRet = SelectParser.parseOrderList(index, wordList, delete
					.getOrders(), Keywords.COMMA);
			check(iRet > index, wordList.get(index), ErrorCode.ERROR,
					ErrorCode.EP_INVALID_EXPRESSION,
					"Failed to parse \"order\" list.");

			index = iRet;
			if (index >= wordList.size()) {
				return new ParseSentenceResult(index, new SQL(delete));
			}
		}

		// parse limit
		Pagination p = new Pagination();
		int iRet = p.parse(index, wordList);
		if (iRet > index) {
			delete.setPagination(p);
			index = iRet;
		}

		if (index >= wordList.size()) {
			return new ParseSentenceResult(index, new SQL(delete));
		}

		// only allow end with ";"
		check(
				(wordList.get(index).getType() == WordDef.DE && Keywords.SENTENCE_DELIMITER
						.equals(wordList.get(index).getWord())), wordList
						.get(index), ErrorCode.ERROR,
				ErrorCode.SP_INVALID_SENTENCE,
				"Expects the end of the sentence.");
		return new ParseSentenceResult(index, new SQL(delete));
	}

	private static void check(boolean bool, Word w, int errorLevel,
			int errorCode, String msg) throws SQLCompilerException {
		if (!bool)
			throw new SQLCompilerException(w, errorLevel, errorCode, msg);
	}

}
