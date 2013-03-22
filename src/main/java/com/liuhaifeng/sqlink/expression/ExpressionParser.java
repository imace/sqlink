package com.liuhaifeng.sqlink.expression;

import java.util.List;
import java.util.Stack;

import com.liuhaifeng.sqlink.beans.Expression;
import com.liuhaifeng.sqlink.beans.SQL;
import com.liuhaifeng.sqlink.config.Keywords;
import com.liuhaifeng.sqlink.config.WordDef;
import com.liuhaifeng.sqlink.error.ErrorCode;
import com.liuhaifeng.sqlink.error.SQLCompilerException;
import com.liuhaifeng.sqlink.semantic.ParseSentenceResult;
import com.liuhaifeng.sqlink.semantic.SelectParser;
import com.liuhaifeng.sqlink.word.Word;

/**
 * Expression parser
 * 
 * @author LiuHaifeng <br>
 *         $Id: ExpressionParser.java 67 2009-12-29 12:29:10Z LiuHaifeng $
 */
public class ExpressionParser {

	public static final int I = -1; // invalid
	public static final int L = 0; // lower
	public static final int E = 1; // equal
	public static final int H = 2; // higher

	private static final int[][] priority = {
			// operator priority. elements indexes according to the Keywords'
			// definition (Keywords.operator).
			/* O1, 2: (, ), ., !, *, /, %, +, -, >, >=,<, <=,==,!=,IN,LK,&&,||,# */
			/* (. */{ L, E, L, L, L, L, L, L, L, L, L, L, L, L, L, L, L, L, L, I },
			/* ). */{ I, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H },
			/* .. */{ H, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H },
			/* !. */{ L, H, L, L, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H },
			/* *. */{ L, H, L, L, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H },
			/* /. */{ L, H, L, L, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H },
			/* %. */{ L, H, L, L, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H, H },
			/* +. */{ L, H, L, L, L, L, L, H, H, H, H, H, H, H, H, H, H, H, H, H },
			/* -. */{ L, H, L, L, L, L, L, H, H, H, H, H, H, H, H, H, H, H, H, H },
			/* >. */{ L, H, L, L, L, L, L, L, L, H, H, H, H, L, L, L, L, H, H, H },
			/* >= */{ L, H, L, L, L, L, L, L, L, H, H, H, H, L, L, L, L, H, H, H },
			/* <. */{ L, H, L, L, L, L, L, L, L, H, H, H, H, L, L, L, L, H, H, H },
			/* <= */{ L, H, L, L, L, L, L, L, L, H, H, H, H, L, L, L, L, H, H, H },
			/* == */{ L, H, L, L, L, L, L, L, L, H, H, H, H, H, H, H, H, H, H, H },
			/* != */{ L, H, L, L, L, L, L, L, L, H, H, H, H, H, H, H, H, H, H, H },
			/* IN */{ L, H, L, L, L, L, L, L, L, H, H, H, H, H, H, H, H, H, H, H },
			/* LK */{ L, H, L, L, L, L, L, L, L, H, H, H, H, H, H, H, H, H, H, H },
			/* && */{ L, H, L, L, L, L, L, L, L, L, L, L, L, L, L, L, L, H, H, H },
			/* || */{ L, H, L, L, L, L, L, L, L, L, L, L, L, L, L, L, L, H, H, H },
			/* #. */{ L, I, L, L, L, L, L, L, L, L, L, L, L, L, L, L, L, L, L, E } };

	/**
	 * Compare priority of two operator.
	 * 
	 * @param opr1
	 * @param opr2
	 * @return
	 * @throws SQLCompilerException
	 */
	public static int comparePriority(String opr1, String opr2)
			throws SQLCompilerException {
		String realOpr1 = Keywords.operatorAliasMap.get(opr1);
		if (realOpr1 == null)
			realOpr1 = opr1;
		String realOpr2 = Keywords.operatorAliasMap.get(opr2);
		if (realOpr2 == null)
			realOpr2 = opr2;
		return priority[getOperatorId(realOpr1)][getOperatorId(realOpr2)];
	}

	/**
	 * Get operator id by a string of the native symbol.
	 * 
	 * @param opr
	 * @return
	 * @throws SQLCompilerException
	 */
	private static int getOperatorId(String opr) throws SQLCompilerException {
		for (int i = 0; i < Keywords.operators.length; i++) {
			if (Keywords.operators[i].equalsIgnoreCase(opr))
				return i;
		}
		throw new SQLCompilerException("Unrecognized operator.");
	}

	/**
	 * Parse expression from the specified position of the given word list.
	 * 
	 * @param index
	 * @param wordList
	 * @return
	 * @throws SQLCompilerException
	 */
	public static ParseExpressionResult parse(int index, List<Word> wordList)
			throws SQLCompilerException {
		Stack<String> oprStack = new Stack<String>();
		Stack<Expression> opdStack = new Stack<Expression>();
		oprStack.push("#");

		Word w = null;
		int parenthesisCount = 0;
		while (index < wordList.size()) {
			w = wordList.get(index);
			try {
				switch (w.getType()) {
				case WordDef.DE:
					if (Keywords.PARENTHESIS_START.equals(w.getWord())) {
						parenthesisCount++;
						processOperator(w.getWord(), oprStack, opdStack);
						index++;
					} else if (Keywords.PARENTHESIS_END.equals(w.getWord())) {
						parenthesisCount--;
						if (parenthesisCount < 0) {
							return new ParseExpressionResult(index, cleanUp(
									oprStack, opdStack));
						} else {
							processOperator(w.getWord(), oprStack, opdStack);
							index++;
						}
					} else {
						return new ParseExpressionResult(index, cleanUp(
								oprStack, opdStack));
					}
					break;
				case WordDef.OP:
					processOperator(w.getWord(), oprStack, opdStack);
					index++;
					break;
				case WordDef.ID:
					Word w2 = null;
					if (wordList.size() > index + 1) {
						w2 = wordList.get(index + 1);
					}
					if (w2 != null && w2.getType() == WordDef.DE
							&& Keywords.PARENTHESIS_START.equals(w2.getWord())) {
						index += 2;
						// parse function
						w2 = wordList.get(index);
						ParseParameterResult ppRet = ParameterParser.parse(
								wordList, index);
						if (ppRet == null || ppRet.getIndex() < index) {
							throw new SQLCompilerException(w2, ErrorCode.ERROR,
									ErrorCode.CG_INVALID_PARAMETER,
									"Failed to parse parameter.");
						}
						Expression exp = new Expression();
						exp.setType(Expression.FUNCTION);
						exp.setName(w.getWord());
						exp.setParameters(ppRet.getParameterList());
						opdStack.push(exp);
						index = ppRet.getIndex();
						w2 = wordList.get(index);
						if (w2.getType() != WordDef.DE
								|| !Keywords.PARENTHESIS_END.equals(w2
										.getWord())) {
							throw new SQLCompilerException(w, ErrorCode.ERROR,
									ErrorCode.CG_UNEXPECTED_WORD,
									"Failed to parse parameter, expects parameters or \""
											+ Keywords.PARENTHESIS_END
											+ "\" here.");
						}
					} else {
						Expression exp = new Expression();
						exp.setType(Expression.REFER);
						exp.setName(w.getWord());
						opdStack.push(exp);
					}
					index++;
					break;
				case WordDef.KEYWORD:
					if (Keywords.SELECT.equalsIgnoreCase(w.getWord())) {
						ParseSentenceResult result = SelectParser.parse(index,
								wordList);
						if (result.getIndex() > index
								&& result.getSentence().getType() == SQL.SELECT
								&& result.getSentence().getSelect() != null) {
							Expression exp = new Expression(result
									.getSentence().getSelect());
							opdStack.push(exp);
							index = result.getIndex();
						} else {
							throw new SQLCompilerException(w, ErrorCode.ERROR,
									ErrorCode.SP_INVALID_SENTENCE,
									"Expects sub query here.");
						}
					} else {
						return new ParseExpressionResult(index, cleanUp(
								oprStack, opdStack));
					}
					break;
				case WordDef.RF:
					Expression exp = new Expression();
					exp.setType(Expression.REFER);
					exp.setName(w.getWord());
					opdStack.push(exp);
					index++;
					break;
				case WordDef.HOLDER:
					opdStack.push(new Expression('?'));
					index++;
					break;
				case WordDef.NU:
					opdStack.push(new Expression());
					index++;
					break;
				case WordDef.BO:
					opdStack.push(new Expression(new Boolean(w.getWord())));
					index++;
					break;
				case WordDef.IN:
					opdStack.push(new Expression(new Integer(w.getWord())));
					index++;
					break;
				case WordDef.LO:
					opdStack.push(new Expression(new Long(w.getWord())));
					index++;
					break;
				case WordDef.FL:
					opdStack.push(new Expression(new Float(w.getWord())));
					index++;
					break;
				case WordDef.DO:
					opdStack.push(new Expression(new Double(w.getWord())));
					index++;
					break;
				case WordDef.ST:
					opdStack.push(new Expression(w.getWord()));
					index++;
					break;
				default:
					return new ParseExpressionResult(index, cleanUp(oprStack,
							opdStack));
				}
			} catch (SQLCompilerException e) {
				if (e.getCompilerError() == null
						|| e.getCompilerError().getLine() == 0) {
					e.getCompilerError().setLine(w.getLine());
					e.getCompilerError().setColumn(w.getColumn());
					e.getCompilerError().setErrorLevel(ErrorCode.ERROR);
				}
				throw e;
			}
			// // Debug
			// System.out.println("OPR: " + oprStack.size() + "\tOPD: "
			// + opdStack.size());
		}

		return new ParseExpressionResult(index, cleanUp(oprStack, opdStack));
	}

	/**
	 * Process operator, e.g. do calculation or push operator to the stack.
	 * 
	 * @param opr
	 * @return
	 * @throws SQLCompilerException
	 */
	private static Expression processOperator(String opr,
			Stack<String> oprStack, Stack<Expression> opdStack)
			throws SQLCompilerException {
		switch (comparePriority(oprStack.peek(), opr)) {
		case L:
			oprStack.push(opr);
			break;
		case E:
			oprStack.pop();
			if ("#".equals(opr)) {
				if (opdStack.size() == 1) {
					return opdStack.pop();
				}
			}
			break;
		case H: // pop operator and operand, add new ternary expression.
			String opr2 = oprStack.pop();
			if (".".equals(opr2)) {
				Expression opd = opdStack.pop();
				if (opd.getType() != Expression.REFER)
					throw new SQLCompilerException("Invalid \".\" operate.");
				Expression opd2 = opdStack.pop();
				if (opd2.getType() != Expression.REFER)
					throw new SQLCompilerException("Invalid \".\" operate.");
				opd.setDomain(opd2.getName());
				opdStack.push(opd);
			} else {
				Expression te = new Expression();
				te.setType(Expression.EXPRESSION);
				te.setName(opr2);
				te.getParameters().add(opdStack.pop());
				if (!Keywords.NOT.equalsIgnoreCase(opr2)) {
					te.getParameters().add(0, opdStack.pop());
				}
				opdStack.push(te);
			}
			return processOperator(opr, oprStack, opdStack);
		case I:
		default:
			throw new SQLCompilerException("Invalid operate.");
		}
		return null;
	}

	/**
	 * Process the remaining operator in the operator stack.
	 * 
	 * @return
	 * @throws SQLCompilerException
	 */
	private static Expression cleanUp(Stack<String> oprStack,
			Stack<Expression> opdStack) throws SQLCompilerException {
		Expression expression = null;
		while (oprStack.size() != 0) {
			try {
				expression = processOperator("#", oprStack, opdStack);
			} catch (SQLCompilerException e) {
				throw new SQLCompilerException("Invalid expression.", e);
			}
		}
		if (opdStack.size() != 0) {
			throw new SQLCompilerException("Invalid expression.");
		}
		return expression;
	}
}
