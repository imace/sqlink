package com.liuhaifeng.sqlink.semantic;

import java.util.List;

import com.liuhaifeng.sqlink.beans.Entity;
import com.liuhaifeng.sqlink.beans.Expression;
import com.liuhaifeng.sqlink.beans.Field;
import com.liuhaifeng.sqlink.beans.Insert;
import com.liuhaifeng.sqlink.beans.SQL;
import com.liuhaifeng.sqlink.config.Keywords;
import com.liuhaifeng.sqlink.config.WordDef;
import com.liuhaifeng.sqlink.error.ErrorCode;
import com.liuhaifeng.sqlink.error.SQLCompilerException;
import com.liuhaifeng.sqlink.expression.ExpressionParser;
import com.liuhaifeng.sqlink.expression.ParseExpressionResult;
import com.liuhaifeng.sqlink.word.Word;

public class InsertParser {

	/**
	 * Parse <b>INSERT</b> statement from the specified position of the given
	 * word list.
	 * 
	 * @param index
	 * @param wordList
	 * @return
	 * @throws SQLCompilerException
	 */
	public static ParseSentenceResult parse(int index, List<Word> wordList)
			throws SQLCompilerException {
		// check "insert" keyword
		Word w = wordList.get(index);
		check((w.getType() == WordDef.KEYWORD && Keywords.INSERT
				.equalsIgnoreCase(w.getWord())), w, ErrorCode.ERROR,
				ErrorCode.CG_UNEXPECTED_WORD, "Expects keyword \""
						+ Keywords.INSERT + "\" here.");

		index++;
		check(index < wordList.size(), w, ErrorCode.ERROR,
				ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

		w = wordList.get(index);
		if (w.getType() == WordDef.KEYWORD
				&& Keywords.INTO.equalsIgnoreCase(w.getWord())) {
			index++;
			check(index < wordList.size(), w, ErrorCode.ERROR,
					ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");
			w = wordList.get(index);
		}

		Insert insert = new Insert();

		// parse target.
		check((w.getType() == WordDef.ID || w.getType() == WordDef.RF), w,
				ErrorCode.ERROR, ErrorCode.CG_UNEXPECTED_WORD,
				"Only expects one valid entity name here.");

		Entity entity = new Entity();
		entity.setEntity(w.getWord());
		insert.setTarget(entity);

		index++;
		check(index < wordList.size(), w, ErrorCode.ERROR,
				ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

		w = wordList.get(index);
		if (w.getType() == WordDef.DE
				&& Keywords.PARENTHESIS_START.equals(w.getWord())) {
			index++;
			check(index < wordList.size(), w, ErrorCode.ERROR,
					ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

			// parse fields.
			int iRet = parseFields(index, wordList, insert.getFields(),
					Keywords.COMMA);
			check(iRet > index, wordList.get(index), ErrorCode.ERROR,
					ErrorCode.EP_INVALID_EXPRESSION,
					"Failed to parse field list.");

			index = iRet;
			check(index < wordList.size(), w, ErrorCode.ERROR,
					ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

			w = wordList.get(index);
			check(w.getType() == WordDef.DE
					&& Keywords.PARENTHESIS_END.equals(w.getWord()), w,
					ErrorCode.ERROR, ErrorCode.CG_UNEXPECTED_WORD, "Expects \""
							+ Keywords.PARENTHESIS_END + "\" here.");

			index++;
			check(index < wordList.size(), w, ErrorCode.ERROR,
					ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");
			w = wordList.get(index);
		}

		// if "values"
		if (w.getType() == WordDef.KEYWORD
				&& Keywords.VALUES.equalsIgnoreCase(w.getWord())) {
			index++;
			// check "(";
			w = wordList.get(index);
			check(w.getType() == WordDef.DE
					&& Keywords.PARENTHESIS_START.equals(w.getWord()), w,
					ErrorCode.ERROR, ErrorCode.CG_UNEXPECTED_WORD, "Expects \""
							+ Keywords.PARENTHESIS_START + "\" here.");

			index++;
			check(index < wordList.size(), w, ErrorCode.ERROR,
					ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

			// parse values
			int iRet = parseValues(index, wordList, insert.getValues(),
					Keywords.COMMA);
			check(iRet > index, wordList.get(index), ErrorCode.ERROR,
					ErrorCode.EP_INVALID_EXPRESSION,
					"Failed to parse value list.");

			index = iRet;
			check(index < wordList.size(), w, ErrorCode.ERROR,
					ErrorCode.SP_INVALID_SENTENCE, "Unexpected end.");

			// check ")"
			w = wordList.get(index);
			check(w.getType() == WordDef.DE
					&& Keywords.PARENTHESIS_END.equals(w.getWord()), w,
					ErrorCode.ERROR, ErrorCode.CG_UNEXPECTED_WORD, "Expects \""
							+ Keywords.PARENTHESIS_END + "\" here.");

			index++;
		} else {
			// parse sub query.
			ParseExpressionResult result = ExpressionParser.parse(index,
					wordList);
			check(
					result.getIndex() > index
							&& result.getExpression() != null
							&& result.getExpression().getType() == Expression.SUB_QUERY,
					w, ErrorCode.ERROR, ErrorCode.SP_INVALID_SENTENCE,
					"Expects sub query here.");

			insert.setSubQuery(result.getExpression().getSubQuery());
			index = result.getIndex();
		}

		check(
				index >= wordList.size()
						|| (wordList.get(index).getType() == WordDef.DE && Keywords.SENTENCE_DELIMITER
								.equals(wordList.get(index).getWord())), w,
				ErrorCode.ERROR, ErrorCode.SP_INVALID_SENTENCE,
				"Expects the end or the sentence delimater.");
		return new ParseSentenceResult(index, new SQL(insert));
	}

	private static int parseFields(int index, List<Word> wordList,
			List<Field> fields, String comma) throws SQLCompilerException {
		while (index < wordList.size()) {
			ParseExpressionResult result = ExpressionParser.parse(index,
					wordList);
			if (result.getIndex() <= index) {
				break;
			} else {
				check(
						result.getExpression() != null
								&& result.getExpression().getType() == Expression.REFER,
						wordList.get(index), ErrorCode.ERROR,
						ErrorCode.SP_INVALID_SENTENCE,
						"Only field references allowed here, and field references can not be empty.");
				Field field = new Field();
				field.setExpression(result.getExpression());
				fields.add(field);

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

	private static int parseValues(int index, List<Word> wordList,
			List<Expression> values, String comma) throws SQLCompilerException {
		while (index < wordList.size()) {
			ParseExpressionResult result = ExpressionParser.parse(index,
					wordList);
			if (result.getIndex() <= index) {
				break;
			} else {
				check(
						result.getExpression() != null
								&& result.getExpression().getType() != Expression.REFER,
						wordList.get(index),
						ErrorCode.ERROR,
						ErrorCode.SP_INVALID_SENTENCE,
						"Only constant values and some simple function allowd here, and do not leave empty too.");
				values.add(result.getExpression());

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
