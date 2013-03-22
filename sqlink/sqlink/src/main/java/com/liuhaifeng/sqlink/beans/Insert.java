package com.liuhaifeng.sqlink.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.liuhaifeng.sqlink.error.SQLRuntimeException;

public class Insert implements Serializable {
	private static final long serialVersionUID = -2560788651773731057L;

	private Entity target;
	private List<Field> fields = new ArrayList<Field>();
	private List<Expression> values = new ArrayList<Expression>();
	private Select subQuery;
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("insert into ");
		sb.append(target);

		// fields ?
		if (fields != null && fields.size() > 0) {
			sb.append("(");
			// fields
			StringBuffer sb2 = new StringBuffer();
			for (Field field : fields) {
				if (field.getExpression().getType() != Expression.REFER)
					throw new SQLRuntimeException(
							"insert field can only be a column referer.");
				sb2.append(", ").append(field.toString());
			}
			sb.append(sb2.substring(2));
			sb.append(")");
		}

		// values ?
		if (values != null && values.size() > 0) {
			sb.append(" values(");
			StringBuffer sb2 = new StringBuffer();
			for (Expression v : values) {
				sb2.append(", ").append(v.toString());
			}
			sb.append(sb2.substring(2));
			sb.append(")");
		} else {
			if (subQuery == null)
				throw new SQLRuntimeException(
						"Invalid \"insert\" statement, no values specified.");
			// sub query (select)
			sb.append(" ").append(subQuery.toString());
		}
		return sb.toString();
	}

	public Entity getTarget() {
		return target;
	}

	public void setTarget(Entity target) {
		this.target = target;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<Expression> getValues() {
		return values;
	}

	public void setValues(List<Expression> values) {
		this.values = values;
	}

	public Select getSubQuery() {
		return subQuery;
	}

	public void setSubQuery(Select subQuery) {
		this.subQuery = subQuery;
	}
}
