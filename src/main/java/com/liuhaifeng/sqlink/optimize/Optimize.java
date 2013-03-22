package com.liuhaifeng.sqlink.optimize;

import java.util.List;

import com.liuhaifeng.sqlink.beans.SQL;
import com.liuhaifeng.sqlink.error.SQLCompilerException;

/**
 * LAS Optimize
 * 
 * @author LiuHaifeng <br>
 *         $Id: Optimize.java 53 2009-12-16 15:41:35Z LiuHaifeng $
 */
public class Optimize {
	// optimize level.
	public static final int NO_OPTIMIZE = 0;
	public static final int BASIC_OPTIMIZE = 1;

	// default optimize level.
	public static final int DEFAULT_OPTIMIZE_LEVEL = BASIC_OPTIMIZE;

	public static void optimize(List<SQL> sqlStatements)
			throws SQLCompilerException {
		optimize(sqlStatements, DEFAULT_OPTIMIZE_LEVEL);
	}

	public static void optimize(List<SQL> sqlStatements, int optimizeLevel)
			throws SQLCompilerException {
		// TODO optimize
	}
}
