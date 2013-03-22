package com.liuhaifeng.sqlink.expression;

import java.io.Serializable;

import com.liuhaifeng.sqlink.beans.Expression;

/**
 * Hold result information of expression parsing.
 * 
 * @author LiuHaifeng <br>
 *         $Id: ParseExpressionResult.java 52 2009-12-16 12:03:57Z LiuHaifeng $
 */
public class ParseExpressionResult implements Serializable {
	private static final long serialVersionUID = -7496393087205881255L;

	private int index;
	private Expression expression;

	public ParseExpressionResult() {
	}

	public ParseExpressionResult(int index, Expression expression) {
		this.index = index;
		this.expression = expression;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}
}
