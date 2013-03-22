package com.liuhaifeng.sqlink.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.liuhaifeng.sqlink.error.SQLRuntimeException;

public class Select implements Serializable {
	private static final long serialVersionUID = -8152555796680419453L;

	private List<Field> objectives = new ArrayList<Field>();
	private List<Entity> targets = new ArrayList<Entity>();
	private List<Join> joins = new ArrayList<Join>();
	private Expression condition;
	private List<Order> orders = new ArrayList<Order>();
	private Pagination pagination;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("select");

		// fields
		StringBuffer sb2 = new StringBuffer();
		for (Field field : objectives) {
			sb2.append(", ").append(field.toString());
		}
		if (sb2.length() < 2)
			throw new SQLRuntimeException("Select field is empty.");
		sb.append(sb2.substring(1));

		// from ?
		if (targets != null && targets.size() > 0) {
			sb.append(" from");
			sb2 = new StringBuffer();
			for (Entity entity : targets) {
				sb2.append(", ").append(entity.toString());
			}
			sb.append(sb2.substring(1));
		}

		// joins
		for (Join join : joins) {
			sb.append(" ").append(join.toString());
		}

		// where
		if (condition != null) {
			sb.append(" where ").append(condition.toString());
		}

		// order
		if (orders != null && orders.size() > 0) {
			sb.append(" order by");
			sb2 = new StringBuffer();
			for (Order order : orders) {
				sb2.append(", ").append(order.toString());
			}
			sb.append(sb2.substring(1));
		}

		// pagination
		if (pagination != null) {
			sb.append(" ").append(pagination.toString());
		}

		return sb.toString();
	}

	public List<Field> getObjectives() {
		return objectives;
	}

	public void setObjectives(List<Field> objectives) {
		this.objectives = objectives;
	}

	public List<Entity> getTargets() {
		return targets;
	}

	public void setTargets(List<Entity> targets) {
		this.targets = targets;
	}

	public List<Join> getJoins() {
		return joins;
	}

	public void setJoins(List<Join> joins) {
		this.joins = joins;
	}

	public Expression getCondition() {
		return condition;
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

}
