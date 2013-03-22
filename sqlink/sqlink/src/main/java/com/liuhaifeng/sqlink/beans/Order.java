package com.liuhaifeng.sqlink.beans;

import java.io.Serializable;

public class Order implements Serializable {
	private static final long serialVersionUID = 241788834702610199L;

	private Expression expression;
	private boolean desc = false;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(expression.toString());
		if (desc) {
			sb.append(" desc");
		} else {
			sb.append(" asc");
		}
		return sb.toString();
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public boolean isDesc() {
		return desc;
	}

	public void setDesc(boolean desc) {
		this.desc = desc;
	}

}
