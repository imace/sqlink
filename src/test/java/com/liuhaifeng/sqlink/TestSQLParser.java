package com.liuhaifeng.sqlink;

import java.io.IOException;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.liuhaifeng.sqlink.beans.Change;
import com.liuhaifeng.sqlink.beans.Delete;
import com.liuhaifeng.sqlink.beans.Entity;
import com.liuhaifeng.sqlink.beans.Expression;
import com.liuhaifeng.sqlink.beans.Field;
import com.liuhaifeng.sqlink.beans.Insert;
import com.liuhaifeng.sqlink.beans.Join;
import com.liuhaifeng.sqlink.beans.Order;
import com.liuhaifeng.sqlink.beans.Pagination;
import com.liuhaifeng.sqlink.beans.SQL;
import com.liuhaifeng.sqlink.beans.Select;
import com.liuhaifeng.sqlink.beans.Update;
import com.liuhaifeng.sqlink.error.SQLCompilerException;
import com.liuhaifeng.sqlink.global.SQLParser;

public class TestSQLParser {
	private static final String SELECT_1 = "select * from table";
	private static final String SELECT_2 = "select * from table where id='abc'";
	private static final String SELECT_3 = "select a.arc_title, a.arc_rank from ph_archives as a where a.arc_rank > 50";
	private static final String SELECT_4 = "select `a`.arc_title, a.`arc_rank`, `a`.`arc_created` from `ph_archives` as `a` where `a`.`arc_rank` <> 50";
	private static final String SELECT_5 = "select `a`.* from ph_archives as a where a.arc_rank != 50 and a.arc_author == 'singer'";
	private static final String SELECT_6 = "select `a`.* from ph_archives as a where a.arc_title is null and a.arc_author is NULL or a.arc_rank = 100";
	private static final String SELECT_7 = "select `a`.*, count(a.arc_id) from ph_archives as a where a.arc_title is null and (a.arc_author is NULL or a.arc_rank = 100)";
	private static final String SELECT_8 = "select count(a.arc_id) as count from ph_archives as a where a.arc_title is not null and (a.arc_author is NULL or a.arc_rank = (1+2))";
	private static final String SELECT_9 = "select count(*) as count from ph_archives order by arc_rank desc, ph_archives.arc_created";
	private static final String SELECT_10 = "select count(*) as count from ph_archives as a order by a.arc_rank, arc_created desc";
	private static final String SELECT_11 = "select * from ph_archives order by a.arc_rank limit 8";
	private static final String SELECT_12 = "select * from ph_archives order by a.arc_rank limit ?, 10";
	private static final String SELECT_13 = "select * from ph_archives where arc_title like 'test%' order by arc_rank, created desc limit 8, 10";
	private static final String SELECT_14 = "select * from ph_archives where arc_id in (select arc_id from ph_archive_tag where tag_id like 'test%')";
	private static final String SELECT_15 = "select * from ph_archives left join ph_archive_tag on ph_archives.arc_id = ph_archive_tag.arc_id where ph_archives.arc_id is not null";
	private static final String SELECT_16 = "select * from ph_archives as a right join ph_archive_tag as at on a.arc_id = concat(at.arc_id, 'a') where a.arc_id like 'test%'";
	private static final String SELECT_17 = "select 'test' as `const`, * from ph_archives as a inner join ph_archive_tag as at on concat('test', ?) <> at.arc_id and at.tag_id is not null where ph_archives.arc_id is not null";
	private static final String SELECT_18 = "select a.*, at.* from ph_archives as `a` left join ph_archive_tag as `at` on `a`.`arc_id` like concat(`at`.`arc_id`, '%')";
	private static final String SELECT_19 = "select a.*, at.*, 20 from ph_archives as `a` right join ph_archive_tag as `at` on `a`.`arc_id` like `at`.`arc_id` order by `a`.`arc_id`";
	private static final String SELECT_20 = "select a.*, at.*, now() from ph_archives as `a` inner join ph_archive_tag as `at` on `a`.`arc_id` is not null or `at`.`arc_id` = ? limit 8";
	private static final String SELECT_21 = "select * from ph_archives left join ph_archive_tag on 1 where 1 limit ?";
	private static final String SELECT_22 = "select 'a', now()";
	private static final String SELECT_23 = "select 'a', now(), * from schema.table where a = ? and b = ? order by a desc limit ?, ?";
	// private static final String SELECT_24 =
	// "select * from (select arc_id from ph_archive_tag where tag_id like 'test%') as tbl";

	private static final String UPDATE_1 = "update table set a = 'b'";
	private static final String UPDATE_2 = "update table as t set t.a = 'b'";
	private static final String UPDATE_3 = "update `table` as t set `t`.`a` = 'b'";
	private static final String UPDATE_4 = "update table set a = 'b', b = 50, c = concat('b', a)";
	private static final String UPDATE_5 = "update table as t set t.a = concat(t.a, ?)";
	private static final String UPDATE_6 = "update table set a = 'b' order by b";
	private static final String UPDATE_7 = "update table set a = 'b' order by b desc limit 8";
	private static final String UPDATE_8 = "update table as t set t.a = 'b' limit ?";
	private static final String UPDATE_9 = "update table set a = 'b' where a is not null";
	private static final String UPDATE_10 = "update table as t set t.a = 'b' where t.a is not null and t.b > ? limit ?, 19";
	private static final String UPDATE_11 = "update table as t set t.a = 'b', `t`.b = concat(`t`.`b`, 'ok') where t.a is not null and t.b >= 0 order by t.`c` desc, t.d limit 1, 19";
	private static final String UPDATE_12 = "update table set a = select a from table2 where a is not null";
	private static final String UPDATE_13 = "update table set a = (select a from table2 where a is not null) where a is null";

	private static final String INSERT_1 = "insert into table values('a', ?, now(), 'c', ?, ?)";
	private static final String INSERT_2 = "insert table(a, b, c, d) values('a', false, 20, NULL)";
	private static final String INSERT_3 = "insert table select 'a', 'b', now(), 'c' from table2";
	private static final String INSERT_4 = "insert into table(a, b, c, d) select a, b, c, d from table2 where a is not null limit 10";

	private static final String DELETE_1 = "delete from table";
	private static final String DELETE_2 = "delete from table where id = 'test'";
	private static final String DELETE_3 = "delete from table order by id";
	private static final String DELETE_4 = "delete from table order by id desc, created desc";
	private static final String DELETE_5 = "delete from table limit ?";
	private static final String DELETE_6 = "delete from table order by id desc, created desc limit 9";
	private static final String DELETE_7 = "delete from table where rank >= 50 limit 9";
	private static final String DELETE_8 = "delete from table where title like 'test%' or rank >= ? order by id, created limit 9";
	// 语法解析支持start 与 size，实际支持情况另论
	private static final String DELETE_9 = "delete from table where rank >= 50 and name like 'test%' order by created desc limit ?, 9";

	// @Ignore
	@Test
	public void testSelect() throws SQLCompilerException, IOException {
		testSQLParser(SELECT_1);
		testSQLParser(SELECT_2);
		testSQLParser(SELECT_3);
		testSQLParser(SELECT_4);
		testSQLParser(SELECT_5);
		testSQLParser(SELECT_6);
		testSQLParser(SELECT_7);
		testSQLParser(SELECT_8);
		testSQLParser(SELECT_9);
		testSQLParser(SELECT_10);
		testSQLParser(SELECT_11);
		testSQLParser(SELECT_12);
		testSQLParser(SELECT_13);
		testSQLParser(SELECT_14);
		testSQLParser(SELECT_15);
		testSQLParser(SELECT_16);
		testSQLParser(SELECT_17);
		testSQLParser(SELECT_18);
		testSQLParser(SELECT_19);
		testSQLParser(SELECT_20);
		testSQLParser(SELECT_21);
		testSQLParser(SELECT_22);
		testSQLParser(SELECT_23);
		// testSQLParser(SELECT_24);
	}

	// @Ignore
	@Test
	public void testUpdate() throws SQLCompilerException, IOException {
		testSQLParser(UPDATE_1);
		testSQLParser(UPDATE_2);
		testSQLParser(UPDATE_3);
		testSQLParser(UPDATE_4);
		testSQLParser(UPDATE_5);
		testSQLParser(UPDATE_6);
		testSQLParser(UPDATE_7);
		testSQLParser(UPDATE_8);
		testSQLParser(UPDATE_9);
		testSQLParser(UPDATE_10);
		testSQLParser(UPDATE_11);
		testSQLParser(UPDATE_12);
		testSQLParser(UPDATE_13);
	}

	// @Ignore
	@Test
	public void testInsert() throws SQLCompilerException, IOException {
		testSQLParser(INSERT_1);
		testSQLParser(INSERT_2);
		testSQLParser(INSERT_3);
		testSQLParser(INSERT_4);
	}

	// @Ignore
	@Test
	public void testDelete() throws SQLCompilerException, IOException {
		testSQLParser(DELETE_1);
		testSQLParser(DELETE_2);
		testSQLParser(DELETE_3);
		testSQLParser(DELETE_4);
		testSQLParser(DELETE_5);
		testSQLParser(DELETE_6);
		testSQLParser(DELETE_7);
		testSQLParser(DELETE_8);
		testSQLParser(DELETE_9);
	}

	public void testSQLParser(String sql) throws SQLCompilerException,
			IOException {
		try {
			SQLParser sqlParser = new SQLParser();
			sqlParser.compile(sql);
			List<SQL> list = sqlParser.getSQLStatements();
			dumpSentences(list);
		} catch (SQLCompilerException e) {
			e.printStackTrace();
			System.err.println(e.getCompilerError().getMessage());
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 2013-03-24: Compile 1 million times Costs: 18574 @ MacBook pro (i7 2.2GHz
	 * Quad, 4G Ram)
	 * 
	 * @throws SQLCompilerException
	 * @throws IOException
	 */
	@Test
	@Ignore
	public void testTimmingSQLParser() throws SQLCompilerException, IOException {
		SQLParser sqlParser = new SQLParser();
		long ts = System.currentTimeMillis();
		for (int i = 0; i < 50000; i++) {
			sqlParser.compile(SELECT_1);
			sqlParser.compile(SELECT_2);
			sqlParser.compile(SELECT_3);
			sqlParser.compile(SELECT_4);
			sqlParser.compile(SELECT_5);
			sqlParser.compile(SELECT_6);
			sqlParser.compile(SELECT_7);
			sqlParser.compile(SELECT_8);
			sqlParser.compile(SELECT_9);
			sqlParser.compile(SELECT_10);
			sqlParser.compile(SELECT_11);
			sqlParser.compile(SELECT_12);
			sqlParser.compile(SELECT_13);
			sqlParser.compile(SELECT_14);
			sqlParser.compile(SELECT_15);
			sqlParser.compile(SELECT_16);
			sqlParser.compile(SELECT_17);
			sqlParser.compile(SELECT_18);
			sqlParser.compile(SELECT_19);
			sqlParser.compile(SELECT_20);
		}
		long cost = System.currentTimeMillis() - ts;
		System.out.print("Cost: " + cost);
	}

	private void dumpSentences(List<SQL> list) {
		System.out.println();
		System.out.println("======================================");
		if (list == null) {
			System.out.println("list is null.");
			return;
		}
		System.out.println("dumping SQL list (" + list.size() + ")");
		for (SQL sql : list) {
			dumpSentence(sql);
		}
	}

	private void dumpSentence(SQL sql) {
		System.out.println("dumping sentence: " + sql);
		if (sql == null)
			return;
		switch (sql.getType()) {
		case SQL.SELECT:
			dumpSelect(sql.getSelect(), "");
			break;
		case SQL.UPDATE:
			dumpUpdate(sql.getUpdate());
			break;
		case SQL.INSERT:
			dumpInsert(sql.getInsert());
			break;
		case SQL.DELETE:
			dumpDelete(sql.getDelete());
			break;
		// case SQL.SEARCH:
		// dumpSearch(sql.getSearch());
		// break;
		}
	}

	private void dumpSelect(Select select, String tabs) {
		System.out.println(tabs + "select");
		dumpFields(select.getObjectives(), tabs + "\t");
		System.out.println(tabs + "from");
		dumpEntities(select.getTargets(), tabs + "\t");
		dumpJoins(select.getJoins(), tabs);
		System.out.println(tabs + "where");
		dumpExpression(select.getCondition(), tabs + "\t");
		System.out.println(tabs + "order by");
		dumpOrders(select.getOrders(), tabs + "\t");
		System.out.println(tabs + "pagination");
		dumpPagination(select.getPagination(), tabs + "\t");
	}

	private void dumpUpdate(Update update) {
		System.out.println("update");
		dumpEntities(update.getTargets(), "\t");
		System.out.println("set");
		dumpChanges(update.getChanges(), "\t");
		System.out.println("where");
		dumpExpression(update.getCondition(), "\t");
		System.out.println("order by");
		dumpOrders(update.getOrders(), "\t");
		System.out.println("pagination");
		dumpPagination(update.getPagination(), "\t");
	}

	private void dumpInsert(Insert insert) {
		System.out.println("insert into");
		dumpEntity(insert.getTarget(), "\t");
		dumpFields(insert.getFields(), "\t\t");
		if (insert.getSubQuery() != null) {
			dumpSelect(insert.getSubQuery(), "\t");
		} else {
			System.out.println("values");
			for (Expression exp : insert.getValues()) {
				dumpExpression(exp, "\t");
			}
		}
	}

	private void dumpDelete(Delete delete) {
		System.out.println("delete from");
		dumpEntity(delete.getTarget(), "\t");
		System.out.println("where");
		dumpExpression(delete.getCondition(), "\t");
		System.out.println("order by");
		dumpOrders(delete.getOrders(), "\t");
		System.out.println("pagination");
		dumpPagination(delete.getPagination(), "\t");
	}

	// private void dumpSearch(Search search) {
	// }

	private void dumpFields(List<Field> fields, String tabs) {
		for (Field field : fields) {
			dumpExpression(field.getExpression(), tabs);
			if (field.getName() != null)
				System.out.println(tabs + "\tAS " + field.getName());
		}
	}

	private void dumpEntities(List<Entity> entities, String tabs) {
		for (Entity entity : entities) {
			dumpEntity(entity, tabs);
		}
	}

	private void dumpEntity(Entity entity, String tabs) {
		System.out.println(tabs + entity.getEntity() + " as "
				+ entity.getName());
	}

	private void dumpJoins(List<Join> joins, String tabs) {
		for (Join join : joins) {
			dumpJoin(join, tabs);
		}
	}

	private void dumpJoin(Join join, String tabs) {
		switch (join.getType()) {
		case Join.LEFT:
			System.out.println(tabs + "left join");
			break;
		case Join.RIGHT:
			System.out.println(tabs + "right join");
			break;
		case Join.INNER:
			System.out.println(tabs + "inner join");
			break;
		default:
			System.out.println("Unknown Join Type (Should be an ERROR).");
		}
		dumpEntity(join.getTarget(), tabs + "\t\t");
		System.out.println(tabs + "\ton");
		dumpExpression(join.getCondition(), tabs + "\t\t");
	}

	private void dumpChanges(List<Change> changes, String tabs) {
		for (Change change : changes) {
			dumpChange(change, tabs);
		}
	}

	public void dumpChange(Change change, String tabs) {
		System.out.println(tabs + change.getDomain() + "." + change.getField()
				+ " = ");
		dumpExpression(change.getValue(), tabs + "\t");
	}

	private void dumpOrders(List<Order> orders, String tabs) {
		for (Order order : orders) {
			dumpExpression(order.getExpression(), tabs);
			System.out.println(tabs + "\tDESC: " + order.isDesc());
		}
	}

	private void dumpPagination(Pagination pagination, String tabs) {
		if (pagination == null) {
			System.out.println(tabs + "No pagination information.");
			return;
		}
		System.out.println(tabs
				+ "start: "
				+ (pagination.getStart() != null ? pagination.getStart()
						: pagination.isStartHolder() ? "?" : null));
		System.out.println(tabs
				+ "size: "
				+ (pagination.getSize() != null ? pagination.getSize()
						: pagination.isSizeHolder() ? "?" : null));
	}

	private void dumpExpression(Expression exp, String tabs) {
		if (exp == null) {
			return;
		}
		switch (exp.getType()) {
		case Expression.NULL:
			System.out.println(tabs + "NULL");
			break;
		case Expression.REFER:
			System.out.println(tabs + "REFER: " + exp.getDomain() + "."
					+ exp.getName());
			break;
		case Expression.HOLDER:
			System.out.println(tabs + "HOLDER: " + exp.getValue());
			break;
		case Expression.FUNCTION:
			System.out.println(tabs + "FUNCTION: " + exp.getName());
			for (Expression e : exp.getParameters()) {
				dumpExpression(e, tabs + "\t\t");
			}
			break;
		case Expression.EXPRESSION:
			List<Expression> parameters = exp.getParameters();
			int i = 0;
			if (parameters.size() > 1) {
				dumpExpression(parameters.get(i++), tabs + "\t");
			}
			System.out.println(tabs + exp.getName());
			for (; i < parameters.size(); i++) {
				dumpExpression(parameters.get(i), tabs + "\t");
			}
			break;
		case Expression.SUB_QUERY:
			dumpSelect(exp.getSubQuery(), tabs);
			break;
		case Expression.BOOLEAN:
			System.out.println(tabs + "BOOLEAN: " + exp.getValue());
			break;
		case Expression.INTEGER:
			System.out.println(tabs + "INTEGER: " + exp.getValue());
			break;
		case Expression.LONG:
			System.out.println(tabs + "LONG: " + exp.getValue());
			break;
		case Expression.FLOAT:
			System.out.println(tabs + "FLOAT: " + exp.getValue());
			break;
		case Expression.DOUBLE:
			System.out.println(tabs + "DOUBLE: " + exp.getValue());
			break;
		case Expression.STRING:
			System.out.println(tabs + "STRING: " + exp.getValue());
			break;
		}
	}
}
