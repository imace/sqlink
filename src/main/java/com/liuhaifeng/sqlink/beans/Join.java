package com.liuhaifeng.sqlink.beans;

import java.io.Serializable;

public class Join implements Serializable {
	private static final long serialVersionUID = -884573751791867055L;

	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int INNER = 3;

	private int type;
	private Entity target;
	private Expression condition;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		switch (type) {
		case LEFT:
			sb.append("left join");
			break;
		case RIGHT:
			sb.append("right join");
			break;
		case INNER:
			sb.append("inner join");
			break;
		}
		sb.append(" ").append(target.toString());
		sb.append(" on ").append(condition.toString());
		return sb.toString();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
}
