package com.liuhaifeng.sqlink.beans;

import java.io.Serializable;

public class Change implements Serializable {
	private static final long serialVersionUID = -944604247926474216L;

	private String domain;
	private String field;
	private Expression value;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (domain != null) {
			sb.append("`").append(domain).append("`.");
		}
		sb.append("`").append(field).append("` = ");
		if (value.getType() == Expression.SUB_QUERY) {
			sb.append("(").append(value.toString()).append(")");
		} else {
			sb.append(value.toString());
		}
		return sb.toString();
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Expression getValue() {
		return value;
	}

	public void setValue(Expression value) {
		this.value = value;
	}
}
