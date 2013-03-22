package com.liuhaifeng.sqlink.beans;

import java.io.Serializable;

public class Entity implements Serializable {
	private static final long serialVersionUID = 559248665712875996L;

	private String domain;
	private String entity;
	private String name;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (domain != null)
			sb.append("`").append(domain).append("`.");
		sb.append("`").append(entity).append("`");
		if (name != null)
			sb.append(" as `").append(name).append("`");
		return sb.toString();
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getDomain() {
		return domain;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
