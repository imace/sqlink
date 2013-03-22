package com.liuhaifeng.sqlink.error;

/**
 * LAS Exception
 * 
 * @author LiuHaifeng <br>
 *         $Id: SQLParserException.java 46 2009-12-04 14:28:03Z LiuHaifeng $
 */
public class SQLParserException extends Exception {
	private static final long serialVersionUID = -8112393161495254852L;

	public SQLParserException() {
		super();
	}

	public SQLParserException(String msg) {
		super(msg);
	}

	public SQLParserException(Throwable e) {
		super(e);
	}

	public SQLParserException(String msg, Throwable e) {
		super(msg, e);
	}
}
