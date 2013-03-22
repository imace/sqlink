package com.liuhaifeng.sqlink.beans;

import java.io.Serializable;

public class Field implements Serializable {
	private static final long serialVersionUID = -3721464458440066446L;

	private Expression expression;
	private String name;
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(expression.toString());
		if (name != null)
			sb.append(" as `").append(name).append("`");
		return sb.toString();
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
