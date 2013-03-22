package com.liuhaifeng.sqlink.semantic;

import java.util.List;

import com.liuhaifeng.sqlink.beans.Entity;
import com.liuhaifeng.sqlink.beans.Expression;
import com.liuhaifeng.sqlink.beans.Field;
import com.liuhaifeng.sqlink.beans.Join;
import com.liuhaifeng.sqlink.beans.Order;
import com.liuhaifeng.sqlink.beans.Pagination;
import com.liuhaifeng.sqlink.beans.SQL;
import com.liuhaifeng.sqlink.beans.Select;
import com.liuhaifeng.sqlink.config.Keywords;
import com.liuhaifeng.sqlink.config.WordDef;
import com.liuhaifeng.sqlink.error.ErrorCode;
import com.liuhaifeng.sqlink.error.SQLCompilerException;
import com.liuhaifeng.sqlink.expression.ExpressionParser;
import com.liuhaifeng.sqlink.expression.ParseExpressionResult;
import com.liuhaifeng.sqlink.word.Word;

public class SelectParser {

	/**
	 * Parse <b>SELECT</b> statement from the specified position of the given
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
		check((w.getType() == WordDef.KEYWORD && Keywords.SELECT
				.equalsIgnoreCase(w.getWord())), w, ErrorCode.ERROR,
				ErrorCode.CG_UNEXPECTED_WORD, "Expects keyword \""
						+ Keywords.SELECT + "\" here.");

		index++;
		check(index < wordList.size(), w, ErrorCode.ERROR,
				ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

		Select select = new Select();

		// parse objectives
		int iRet = parseObjectives(index, wordList, select.getObjectives(),
				Keywords.COMMA);
		check(iRet > index, wordList.get(index), ErrorCode.ERROR,
				ErrorCode.EP_INVALID_EXPRESSION,
				"Failed to parse objective list.");

		index = iRet;
		if (index >= wordList.size()
				|| (wordList.get(index).getType() == WordDef.DE && Keywords.SENTENCE_DELIMITER
						.equals(wordList.get(index).getWord()))) {
			return new ParseSentenceResult(index, new SQL(select));
		}

		// check "from"
		w = wordList.get(index);
		check((w.getType() == WordDef.KEYWORD && Keywords.FROM
				.equalsIgnoreCase(w.getWord())), w, ErrorCode.ERROR,
				ErrorCode.CG_UNEXPECTED_WORD, "Only expects \"" + Keywords.FROM
						+ "\"");

		index++;
		check(index < wordList.size(), w, ErrorCode.ERROR,
				ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

		// parse targets
		iRet = parseTargets(index, wordList, select.getTargets(),
				Keywords.COMMA);
		check(iRet > index, wordList.get(index), ErrorCode.ERROR,
				ErrorCode.EP_INVALID_EXPRESSION,
				"Failed to parse targets list.");

		index = iRet;
		if (index >= wordList.size()) {
			return new ParseSentenceResult(index, new SQL(select));
		}

		// parse joins
		iRet = parseJoins(index, wordList, select.getJoins());
		index = iRet; // there may has no any join
		if (index >= wordList.size()) {
			return new ParseSentenceResult(index, new SQL(select));
		}

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
			select.setCondition(result.getExpression());

			index = result.getIndex();
			if (index >= wordList.size()) {
				return new ParseSentenceResult(index, new SQL(select));
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
			iRet = parseOrderList(index, wordList, select.getOrders(),
					Keywords.COMMA);
			check(iRet > index, wordList.get(index), ErrorCode.ERROR,
					ErrorCode.EP_INVALID_EXPRESSION,
					"Failed to parse \"order\" list.");

			index = iRet;
			if (index >= wordList.size()) {
				return new ParseSentenceResult(index, new SQL(select));
			}
		}

		// parse limit
		Pagination p = new Pagination();
		iRet = p.parse(index, wordList);
		if (iRet > index) {
			select.setPagination(p);
			index = iRet;
		}

		if (index >= wordList.size()) {
			return new ParseSentenceResult(index, new SQL(select));
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
		return new ParseSentenceResult(index, new SQL(select));
	}

	private static int parseObjectives(int index, List<Word> wordList,
			List<Field> objectives, String comma) throws SQLCompilerException {
		while (index < wordList.size()) {
			ParseExpressionResult result = ExpressionParser.parse(index,
					wordList);
			if (result.getIndex() <= index) {
				break;
			} else {
				Field field = new Field();
				field.setExpression(result.getExpression());
				index = result.getIndex();
				if (index < wordList.size()) {

					Word w = wordList.get(index);
					if (w.getType() == WordDef.KEYWORD
							&& Keywords.AS.equalsIgnoreCase(w.getWord())) {
						index++;
						check(index < wordList.size(), w, ErrorCode.ERROR,
								ErrorCode.SP_INVALID_SENTENCE,
								"Unexpected end.");

						w = wordList.get(index);
						check(w.getType() == WordDef.ID
								|| w.getType() == WordDef.RF, w,
								ErrorCode.ERROR, ErrorCode.SP_INVALID_SENTENCE,
								"Invalid sql sentence, expects alias after 'AS'.");
						field.setName(w.getWord());
						index++;
					}
				}
				objectives.add(field);
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

	public static int parseTargets(int index, List<Word> wordList,
			List<Entity> targets, String comma) throws SQLCompilerException {
		while (index < wordList.size()) {
			ParseExpressionResult result = ExpressionParser.parse(index,
					wordList);
			if (result.getIndex() <= index) {
				break;
			} else if (result.getExpression() == null
					|| result.getExpression().getType() != Expression.REFER) {
				throw new SQLCompilerException(wordList.get(index),
						ErrorCode.ERROR, ErrorCode.EP_INVALID_EXPRESSION,
						"Only reference expects here.");
			} else {
				Entity entity = new Entity();
				entity.setDomain(result.getExpression().getDomain());
				entity.setEntity(result.getExpression().getName());
				index = result.getIndex();
				if (index < wordList.size()) {

					Word w = wordList.get(index);
					if (w.getType() == WordDef.KEYWORD
							&& Keywords.AS.equalsIgnoreCase(w.getWord())) {
						index++;
						check(index < wordList.size(), w, ErrorCode.ERROR,
								ErrorCode.SP_INVALID_SENTENCE,
								"Unexpected end.");

						w = wordList.get(index);
						check(w.getType() == WordDef.ID
								|| w.getType() == WordDef.RF, w,
								ErrorCode.ERROR, ErrorCode.SP_INVALID_SENTENCE,
								"Invalid sql sentence, expects alias after 'AS'.");
						entity.setName(w.getWord());
						index++;
					}
				}
				targets.add(entity);
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

	private static int parseJoins(int index, List<Word> wordList,
			List<Join> joins) throws SQLCompilerException {
		/**
		 * 0 start, 1 got entity, 2 got as, 3 got alias, 4 got on
		 */
		int state = 0;
		Join join = null;

		while (index < wordList.size()) {
			Word w = wordList.get(index);
			switch (state) {
			case 0:
				if (w.getType() != WordDef.KEYWORD) {
					return index;
				}
				if (Keywords.LEFT.equalsIgnoreCase(w.getWord())) {
					join = new Join();
					join.setType(Join.LEFT);
				} else if (Keywords.RIGHT.equalsIgnoreCase(w.getWord())) {
					join = new Join();
					join.setType(Join.RIGHT);
				} else if (Keywords.INNER.equalsIgnoreCase(w.getWord())) {
					join = new Join();
					join.setType(Join.INNER);
				} else {
					return index;
				}
				index++;
				check(index < wordList.size(), w, ErrorCode.ERROR,
						ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

				w = wordList.get(index);
				check(w.getType() == WordDef.KEYWORD
						&& Keywords.JOIN.equalsIgnoreCase(w.getWord()), w,
						ErrorCode.ERROR, ErrorCode.CG_UNEXPECTED_WORD,
						"Expects keyword \"" + Keywords.JOIN + "\" here.");

				index++;
				check(index < wordList.size(), w, ErrorCode.ERROR,
						ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

				w = wordList.get(index);
				check(w.getType() == WordDef.ID || w.getType() == WordDef.RF,
						w, ErrorCode.ERROR, ErrorCode.CG_UNEXPECTED_WORD,
						"Expects entity name here.");

				Entity entity = new Entity();
				entity.setEntity(w.getWord());
				join.setTarget(entity);

				joins.add(join);
				state = 1;
				index++;
				check(index < wordList.size(), w, ErrorCode.ERROR,
						ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");
				break;
			case 1:
				if (w.getType() == WordDef.KEYWORD
						&& Keywords.AS.equalsIgnoreCase(w.getWord())) {
					index++;
					state = 2;
				} else if (w.getType() == WordDef.KEYWORD
						&& Keywords.ON.equalsIgnoreCase(w.getWord())) {
					index++;
					state = 4;
				} else {
					throw new SQLCompilerException(w, ErrorCode.ERROR,
							ErrorCode.CG_UNEXPECTED_WORD, "Expects keyword \""
									+ Keywords.AS + "\" or \"" + Keywords.ON
									+ "\" here.");
				}
				check(index < wordList.size(), w, ErrorCode.ERROR,
						ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");
				break;
			case 2:
				check(w.getType() == WordDef.ID || w.getType() == WordDef.RF,
						w, ErrorCode.ERROR, ErrorCode.CG_UNEXPECTED_WORD,
						"Expects alias.");
				join.getTarget().setName(w.getWord());
				index++;
				state = 3;
				check(index < wordList.size(), w, ErrorCode.ERROR,
						ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");
				break;
			case 3:
				check(w.getType() == WordDef.KEYWORD
						&& Keywords.ON.equalsIgnoreCase(w.getWord()), w,
						ErrorCode.ERROR, ErrorCode.CG_UNEXPECTED_WORD,
						"Only expects keyword \"" + Keywords.ON + "\" here.");
				index++;
				state = 4;
				check(index < wordList.size(), w, ErrorCode.ERROR,
						ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");
				break;
			case 4:
				ParseExpressionResult result = ExpressionParser.parse(index,
						wordList);
				check(result.getIndex() > index
						&& result.getExpression() != null, w, ErrorCode.ERROR,
						ErrorCode.SP_INVALID_SENTENCE,
						"Failed to parse join condition.");
				join.setCondition(result.getExpression());

				index = result.getIndex();
				state = 0;
				break;
			default:
				throw new SQLCompilerException("Enter an unknown state.");
			}
		}
		return index;
	}

	public static int parseOrderList(int index, List<Word> wordList,
			List<Order> orders, String comma) throws SQLCompilerException {
		while (index < wordList.size()) {
			ParseExpressionResult result = ExpressionParser.parse(index,
					wordList);
			if (result.getIndex() <= index) {
				break;
			} else {
				Order order = new Order();
				order.setExpression(result.getExpression());
				index = result.getIndex();
				if (index < wordList.size()) {
					Word w = wordList.get(index);
					if (w.getType() == WordDef.KEYWORD
							&& Keywords.ASC.equalsIgnoreCase(w.getWord())) {
						order.setDesc(false);
						index++;
					} else if (w.getType() == WordDef.KEYWORD
							&& Keywords.DESC.equalsIgnoreCase(w.getWord())) {
						order.setDesc(true);
						index++;
					}
				}
				orders.add(order);
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
