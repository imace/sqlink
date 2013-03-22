package com.liuhaifeng.sqlink.error;

import java.io.Serializable;

/**
 * Error Code
 * 
 * @author LiuHaifeng <br>
 *         $Id: ErrorCode.java 46 2009-12-04 14:28:03Z LiuHaifeng $
 */
public class ErrorCode implements Serializable {
	private static final long serialVersionUID = 6458837997235974418L;

	// --- {{{ error level section {{{ ---
	public static final int INFO = 0;
	public static final int WARN = 1;
	public static final int ERROR = 2;
	public static final int FATAL = 3;
	// --- }}} error level section }}} ---

	// --- {{{ compiler global section {{{ ---
	public static final int CG_DEAD_LOOP = 101;
	public static final int CG_UNEXPECTED_WORD = 102;
	public static final int CG_INVALID_PARAMETER = 4001;
	// --- }}} compiler global section }}} ---

	// --- {{{ word parser section {{{ ---
	public static final int WP_INVALID_SOURCE = 1001;
	public static final int WP_INVALID_SYMBOL = 1002;
	public static final int WP_INVALID_STRING = 1003;
	// --- }}} word parser section }}} ---

	// --- {{{ domain parser section {{{ ---
	public static final int DP_INVALID_DOMAIN = 2001;
	public static final int DP_INVALID_ENTITY = 2002;
	public static final int DP_INVALID_DOMAIN_CONTENT = 2003;
	// --- }}} domain parser section }}} ---

	// --- {{{ state entity parser section {{{ ---
	public static final int SEP_INVALID_STATE = 3001;
	public static final int SEP_INVALID_CONTENT = 3002;
	// --- }}} state entity parser section }}} ---

	// --- {{{ doing entity parser section {{{ ---
	public static final int DEP_INVALID_DOING = 4001;
	public static final int DEP_INVALID_CONTENT = 4002;
	// --- }}} doing entity parser section }}} ---

	// --- {{{ listening entity parser section {{{ ---
	public static final int LEP_INVALID_LISTENING = 5001;
	public static final int LEP_INVALID_CONTENT = 5002;
	// --- }}} listening entity parser section }}} ---

	// --- {{{ sentence parser section {{{ ---
	public static final int SP_INVALID_SENTENCE = 6001;
	// --- }}} sentence parser section }}} ---

	// --- {{{ expression parser section {{{ ---
	public static final int EP_INVALID_EXPRESSION = 7001;
	// --- }}} expression parser section }}} ---

	// --- {{{ section {{{ ---
	// --- }}} section }}} ---

	// --- {{{ section {{{ ---
	// --- }}} section }}} ---

	// --- {{{ section {{{ ---
	// --- }}} section }}} ---
}
