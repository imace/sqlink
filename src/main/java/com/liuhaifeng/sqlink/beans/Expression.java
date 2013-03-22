package com.liuhaifeng.sqlink.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.liuhaifeng.sqlink.config.Keywords;
import com.liuhaifeng.sqlink.error.SQLCompilerException;
import com.liuhaifeng.sqlink.error.SQLRuntimeException;
import com.liuhaifeng.sqlink.expression.ExpressionParser;

public class Expression implements Serializable {
	private static final long serialVersionUID = 4612459975557668893L;

	// type
	public static final int NULL = 0;
	public static final int REFER = 2;
	public static final int FUNCTION = 3;
	public static final int EXPRESSION = 4;
	public static final int SUB_QUERY = 5;
	public static final int HOLDER = 6; // "?" 占位符

	public static final int BOOLEAN = 11;
	public static final int INTEGER = 12;
	public static final int LONG = 13;
	public static final int FLOAT = 14;
	public static final int DOUBLE = 15;
	public static final int STRING = 16;

	// operator
	public static final String AND = Keywords.AND;
	public static final String OR = Keywords.OR;
	public static final String LESS_THAN = "<";
	public static final String LESS_OR_EQUAL = "<=";
	public static final String EQUAL = "=";
	public static final String GREAT_OR_EQUAL = ">=";
	public static final String GREAT_THAN = ">";
	public static final String NOT_EUQAL = "<>";
	public static final String LIKE = Keywords.LIKE;
	public static final String IS = Keywords.IS;
	public static final String NOT = Keywords.NOT;
	public static final String IN = Keywords.IN;

	private int type = NULL;
	private String domain; // domain or entity name
	private String name; // refer name,function name or operator
	private List<Expression> parameters = new ArrayList<Expression>();
	private Object value; // constant value
	private Select subQuery;

	public Expression() {
		this.type = NULL;
	}

	public Expression(boolean b) {
		this.type = BOOLEAN;
		value = b;
	}

	public Expression(int i) {
		this.type = INTEGER;
		value = i;
	}

	public Expression(long l) {
		this.type = LONG;
		value = l;
	}

	public Expression(float f) {
		this.type = FLOAT;
		value = f;
	}

	public Expression(double d) {
		this.type = DOUBLE;
		value = d;
	}

	public Expression(String string) {
		this.type = STRING;
		value = string;
	}
	
	public Expression(char ch) {
		if (ch == '?') {
			this.type = HOLDER;
			value = ch;
		} else {
			this.type = STRING;
			value = ch;
		}
	}

	public Expression(Select select) {
		this.type = SUB_QUERY;
		this.setSubQuery(select);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		switch (type) {
		case Expression.NULL:
			sb.append("null");
			break;
		case Expression.REFER:
			if (domain != null) {
				sb.append("`").append(domain).append("`.");
			}
			if (!"*".equals(name)) {
				sb.append("`").append(name).append("`");
			} else {
				sb.append(name);
			}
			break;
		case Expression.FUNCTION:
			sb.append(name).append("(");
			StringBuffer sb2 = new StringBuffer();
			for (Expression exp : parameters) {
				sb2.append(", ").append(exp.toString());
			}
			if (sb2.length() > 2) {
				sb.append(sb2.substring(2));
			}
			sb.append(")");
			break;
		case Expression.EXPRESSION:
			Iterator<Expression> it = parameters.iterator();
			Expression exp = it.next();
			if (parameters.size() == 2) {
				if (exp.getType() == Expression.EXPRESSION) {
					int priority;
					try {
						priority = ExpressionParser.comparePriority(exp
								.getName(), name);
					} catch (SQLCompilerException e) {
						throw new SQLRuntimeException(e);
					}
					switch (priority) {
					case ExpressionParser.H:
						sb.append(exp.toString());
						break;
					default:
						sb.append("(").append(exp.toString()).append(")");
					}
				} else if (exp.getType() == Expression.SUB_QUERY) {
					sb.append("(").append(exp.toString()).append(")");
				} else {
					sb.append(exp.toString());
				}
				sb.append(" ");
				exp = it.next();
			}
			sb.append(name).append(" ");
			if (exp.getType() == Expression.EXPRESSION) {
				int priority;
				try {
					priority = ExpressionParser.comparePriority(name, exp
							.getName());
				} catch (SQLCompilerException e) {
					throw new SQLRuntimeException(e);
				}
				switch (priority) {
				case ExpressionParser.L:
					sb.append(exp.toString());
					break;
				default:
					sb.append("(").append(exp.toString()).append(")");
				}
			} else if (exp.getType() == Expression.SUB_QUERY) {
				sb.append("(").append(exp.toString()).append(")");
			} else {
				sb.append(exp.toString());
			}
			break;
		case Expression.SUB_QUERY:
			sb.append(subQuery.toString());
			break;
		case Expression.BOOLEAN:
		case Expression.INTEGER:
		case Expression.LONG:
		case Expression.FLOAT:
		case Expression.DOUBLE:
			sb.append(value);
			break;
		case Expression.HOLDER:
			sb.append(value);
			break;
		case Expression.STRING:
			sb.append("'").append(toSafeString((String) value)).append("'");
			break;
		}
		return sb.toString();
	}

	private String toSafeString(String string) {
		if (string == null)
			return "";
		return string.replace("'", "\\\'");
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Expression> getParameters() {
		return parameters;
	}

	public void setParameters(List<Expression> parameters) {
		this.parameters = parameters;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setSubQuery(Select subQuery) {
		this.subQuery = subQuery;
	}

	public Select getSubQuery() {
		return subQuery;
	}

}
