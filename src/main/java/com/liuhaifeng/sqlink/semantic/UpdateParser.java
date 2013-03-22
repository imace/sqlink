package com.liuhaifeng.sqlink.semantic;

import java.util.List;

import com.liuhaifeng.sqlink.beans.Change;
import com.liuhaifeng.sqlink.beans.Expression;
import com.liuhaifeng.sqlink.beans.Pagination;
import com.liuhaifeng.sqlink.beans.SQL;
import com.liuhaifeng.sqlink.beans.Update;
import com.liuhaifeng.sqlink.config.Keywords;
import com.liuhaifeng.sqlink.config.WordDef;
import com.liuhaifeng.sqlink.error.ErrorCode;
import com.liuhaifeng.sqlink.error.SQLCompilerException;
import com.liuhaifeng.sqlink.expression.ExpressionParser;
import com.liuhaifeng.sqlink.expression.ParseExpressionResult;
import com.liuhaifeng.sqlink.word.Word;

public class UpdateParser {

	/**
	 * Parse <b>UPDATE</b> statement from the specified position of the given
	 * word list.
	 * 
	 * @param index
	 * @param wordList
	 * @return
	 * @throws SQLCompilerException
	 */
	public static ParseSentenceResult parse(int index, List<Word> wordList)
			throws SQLCompilerException {
		// check "select" keyword
		Word w = wordList.get(index);
		check((w.getType() == WordDef.KEYWORD && Keywords.UPDATE
				.equalsIgnoreCase(w.getWord())), w, ErrorCode.ERROR,
				ErrorCode.CG_UNEXPECTED_WORD, "Expects keyword \""
						+ Keywords.UPDATE + "\" here.");

		index++;
		check(index < wordList.size(), w, ErrorCode.ERROR,
				ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

		Update update = new Update();

		// parse targets
		int iRet = SelectParser.parseTargets(index, wordList, update
				.getTargets(), Keywords.COMMA);
		check(iRet > index, wordList.get(index), ErrorCode.ERROR,
				ErrorCode.EP_INVALID_EXPRESSION,
				"Failed to parse targets list.");

		index = iRet;
		check(index < wordList.size(), w, ErrorCode.ERROR,
				ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

		// check "set"
		w = wordList.get(index);
		check((w.getType() == WordDef.KEYWORD && Keywords.SET
				.equalsIgnoreCase(w.getWord())), w, ErrorCode.ERROR,
				ErrorCode.CG_UNEXPECTED_WORD, "Only expects keyword \""
						+ Keywords.SET + "\" here.");

		index++;
		check(index < wordList.size(), w, ErrorCode.ERROR,
				ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

		// parse "changes"
		iRet = parseChanges(index, wordList, update.getChanges(),
				Keywords.COMMA);
		check(iRet > index, wordList.get(index), ErrorCode.ERROR,
				ErrorCode.EP_INVALID_EXPRESSION,
				"Failed to parse changes list.");

		index = iRet;
		if (index >= wordList.size()) {
			return new ParseSentenceResult(index, new SQL(update));
		}

		// TODO check joins
		// ...

		// check "where"
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
			update.setCondition(result.getExpression());

			index = result.getIndex();
			if (index >= wordList.size()) {
				return new ParseSentenceResult(index, new SQL(update));
			}
		}

		// check "order by"
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
			iRet = SelectParser.parseOrderList(index, wordList, update
					.getOrders(), Keywords.COMMA);
			check(iRet > index, wordList.get(index), ErrorCode.ERROR,
					ErrorCode.EP_INVALID_EXPRESSION,
					"Failed to parse \"order\" list.");

			index = iRet;
			if (index >= wordList.size()) {
				return new ParseSentenceResult(index, new SQL(update));
			}
		}

		// parse limit
		Pagination p = new Pagination();
		iRet = p.parse(index, wordList);
		if (iRet > index) {
			update.setPagination(p);
			index = iRet;
		}

		if (index >= wordList.size()) {
			return new ParseSentenceResult(index, new SQL(update));
		}

		// allow select sentence ends with ";" as a regular sentence or ends
		// with ")" as a sub query.
		check(
				(wordList.get(index).getType() == WordDef.DE && (Keywords.SENTENCE_DELIMITER
						.equals(wordList.get(index).getWord()) || Keywords.PARENTHESIS_END
						.equals(wordList.get(index).getWord()))), wordList
						.get(index), ErrorCode.ERROR,
				ErrorCode.SP_INVALID_SENTENCE,
				"Expects the end of the sentence.");
		return new ParseSentenceResult(index, new SQL(update));
	}

	private static int parseChanges(int index, List<Word> wordList,
			List<Change> changes, String comma) throws SQLCompilerException {
		while (index < wordList.size()) {
			ParseExpressionResult result = ExpressionParser.parse(index,
					wordList);
			if (result.getIndex() <= index) {
				break;
			} else {
				Expression exp = result.getExpression();
				check((exp != null && exp.getType() == Expression.EXPRESSION
						&& Keywords.ASSIGN.equals(exp.getName())
						&& exp.getParameters().size() == 2 && exp
						.getParameters().get(0).getType() == Expression.REFER),
						wordList.get(index), ErrorCode.ERROR,
						ErrorCode.SP_INVALID_SENTENCE,
						"Invalid assignment expression.");

				Change change = new Change();
				Expression field = exp.getParameters().get(0);
				change.setDomain(field.getDomain());
				change.setField(field.getName());
				change.setValue(exp.getParameters().get(1));
				changes.add(change);

				index = result.getIndex();

				if (index < wordList.size()) {
					Word w = wordList.get(index);
					if (w.getType() == WordDef.DE && comma.equals(w.getWord())) {
						index++;
					}
				}
			}
		}
		return index;
	}

	private static void check(boolean bool, Word w, int errorLevel,
			int errorCode, String msg) throws SQLCompilerException {
		if (!bool)
			throw new SQLCompilerException(w, errorLevel, errorCode, msg);
	}

}
