package com.liuhaifeng.sqlink.beans;

import java.io.Serializable;

/**
 * Domain.
 * 
 * @author LiuHaifeng <br>
 *         $Id: SQL.java 62 2009-12-22 08:01:00Z LiuHaifeng $
 */
public class SQL implements Serializable {
	private static final long serialVersionUID = 4319228489572398294L;

	public static final int UNKNOWN = 0;
	public static final int SELECT = 1;
	public static final int UPDATE = 2;
	public static final int INSERT = 3;
	public static final int DELETE = 4;
	public static final int SEARCH = 5;// unimplemented yet

	private int type = UNKNOWN;

	private Select select;
	private Update update;
	private Insert insert;
	private Delete delete;
	
	@Override
	public String toString() {
		switch (type) {
		case UNKNOWN:
			return "Unknown SQL.";
		case SELECT:
			return select.toString();
		case UPDATE:
			return update.toString();
		case INSERT:
			return insert.toString();
		case DELETE:
			return delete.toString();
		case SEARCH:
			return null;
		default:
			return super.toString();
		}
	}

	public SQL(Select select) {
		this.type = SELECT;
		this.select = select;
	}

	public SQL(Update update) {
		this.type = UPDATE;
		this.update = update;
	}

	public SQL(Insert insert) {
		this.type = INSERT;
		this.insert = insert;
	}

	public SQL(Delete delete) {
		this.type = DELETE;
		this.delete = delete;
	}

	public int getType() {
		return type;
	}

	protected void setType(int type) {
		this.type = type;
	}

	public Select getSelect() {
		return select;
	}

	protected void setSelect(Select select) {
		this.select = select;
	}

	public Update getUpdate() {
		return update;
	}

	protected void setUpdate(Update update) {
		this.update = update;
	}

	public Insert getInsert() {
		return insert;
	}

	protected void setInsert(Insert insert) {
		this.insert = insert;
	}

	public Delete getDelete() {
		return delete;
	}

	protected void setDelete(Delete delete) {
		this.delete = delete;
	}

}
