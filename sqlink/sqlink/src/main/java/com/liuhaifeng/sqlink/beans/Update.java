package com.liuhaifeng.sqlink.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Update implements Serializable {
	private static final long serialVersionUID = 1296165164336661356L;

	private List<Entity> targets = new ArrayList<Entity>();
	private List<Change> changes = new ArrayList<Change>();
	private Expression condition;
	private List<Order> orders = new ArrayList<Order>();
	private Pagination pagination;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("update");

		// targets
		StringBuffer sb2 = new StringBuffer();
		for (Entity entity : targets) {
			sb2.append(", ").append(entity.toString());
		}
		sb.append(sb2.substring(1));

		// changes
		sb.append(" set");
		sb2 = new StringBuffer();
		for (Change change : changes) {
			sb2.append(", ").append(change.toString());
		}
		sb.append(sb2.substring(1));

		// where ?
		if (condition != null) {
			sb.append(" where ").append(condition.toString());
		}

		// order ?
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

	public List<Entity> getTargets() {
		return targets;
	}

	public void setTargets(List<Entity> targets) {
		this.targets = targets;
	}

	public List<Change> getChanges() {
		return changes;
	}

	public void setChanges(List<Change> changes) {
		this.changes = changes;
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
