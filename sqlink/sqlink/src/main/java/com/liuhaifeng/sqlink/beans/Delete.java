package com.liuhaifeng.sqlink.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Delete implements Serializable {
	private static final long serialVersionUID = -7077385209910376573L;

	private Entity target;
	private Expression condition;
	private List<Order> orders = new ArrayList<Order>();
	private Pagination pagination;
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("delete from ");

		// target
		sb.append(target.toString());

		// where ?
		if (condition != null) {
			sb.append(" where ").append(condition.toString());
		}

		// order ?
		if (orders != null && orders.size() > 0) {
			sb.append(" order by");
			StringBuffer sb2 = new StringBuffer();
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

	public Entity getTarget() {
		return target;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}

	public Expression getCondition() {
		return condition;
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}
}
