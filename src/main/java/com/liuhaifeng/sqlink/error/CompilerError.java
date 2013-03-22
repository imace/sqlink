package com.liuhaifeng.sqlink.error;

import java.io.Serializable;

/**
 * Compiler Error
 * 
 * @author LiuHaifeng <br>
 *         $Id: CompilerError.java 46 2009-12-04 14:28:03Z LiuHaifeng $
 */
public class CompilerError implements Serializable {
	private static final long serialVersionUID = 3527843702090659093L;

	private String sourceName;
	private int line;
	private int column;
	private int errorLevel;
	private int errorCode;
	private String message;

	public CompilerError() {
	}

	public CompilerError(int line, int column, int errorLevel, int errorCode,
			String message) {
		this.line = line;
		this.column = column;
		this.errorLevel = errorLevel;
		this.errorCode = errorCode;
		this.message = message;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getErrorLevel() {
		return errorLevel;
	}

	public void setErrorLevel(int errorLevel) {
		this.errorLevel = errorLevel;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
