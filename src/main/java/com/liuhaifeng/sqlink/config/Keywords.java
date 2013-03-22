package com.liuhaifeng.sqlink.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Key words.
 * 
 * @author LiuHaifeng <br>
 *         $Id: Keywords.java 61 2009-12-19 17:08:26Z LiuHaifeng $
 */
public class Keywords {
	public static final String NULL = "null";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String IS = "is";
	public static final String NOT = "not";

	public static final String SELECT = "select";
	public static final String UPDATE = "update";
	public static final String INSERT = "insert";
	public static final String DELETE = "delete";
	public static final String SEARCH = "search";
	public static final String SET = "set";
	public static final String INTO = "into";
	public static final String VALUES = "values";
	public static final String FROM = "from";
	public static final String AS = "as";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String INNER = "inner";
	public static final String JOIN = "join";
	public static final String ON = "on";
	public static final String WHERE = "where";
	public static final String IN = "in";
	public static final String LIKE = "like";
	public static final String AND = "and";
	public static final String OR = "or";
	public static final String ORDER = "order";
	public static final String BY = "by";
	public static final String ASC = "asc";
	public static final String DESC = "desc";
	public static final String LIMIT = "limit";

	public static final String COUNT = "count";
	public static final String SUM = "sum";
	public static final String MAX = "max";
	public static final String MIN = "min";

	public static final String SENTENCE_DELIMITER = ";";
	public static final String PARENTHESIS_START = "(";
	public static final String PARENTHESIS_END = ")";
	public static final String COMMA = ",";
	public static final String ASSIGN = "=";

	public static final String[] keywords = { NULL, TRUE, FALSE, IS, NOT,
			SELECT, UPDATE, INSERT, DELETE, SEARCH, SET, INTO, VALUES, FROM,
			AS, LEFT, RIGHT, INNER, JOIN, ON, WHERE, IN, LIKE, AND, OR, ORDER,
			BY, ASC, DESC, LIMIT };

	public static final String[] operators = {
	/* OPR_PAR_LEFT */"(",
	/* OPR_PAR_RIGHT */")",
	/* OPR_DOT */".",
	/* OPR_NOT */"!",
	/* OPR_MUL */"*",
	/* OPR_DIV */"/",
	/* OPR_MOD */"%",
	/* OPR_ADD */"+",
	/* OPR_SUB */"-",
	/* OPR_GREATER */">",
	/* OPR_GREATER_EQUAL */">=",
	/* OPR_LESS */"<",
	/* OPR_LESS_EQUAL */"<=",
	/* OPR_EQUAL */"==",
	/* OPR_NOT_EQUAL */"!=",
	/* OPR_IN */IN,
	/* OPR_LIKE */LIKE,
	/* OPR_NOT_EQUAL */"&&",
	/* OPR_NOT_EQUAL */"||",
	/* OPR_POUND */"#" };

	public static final Map<String, String> operatorAliasMap = new HashMap<String, String>();

	static {
		operatorAliasMap.put(AND, "&&");
		operatorAliasMap.put(OR, "||");
		operatorAliasMap.put(IS, "==");
		operatorAliasMap.put(NOT, "!");
		operatorAliasMap.put("=", "==");
		operatorAliasMap.put("<>", "!=");
	}

	public static final String[] functions = { COUNT, SUM, MAX, MIN }; // unused
	// yet.
}
