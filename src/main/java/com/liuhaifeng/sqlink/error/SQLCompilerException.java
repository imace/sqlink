package com.liuhaifeng.sqlink.error;

import com.liuhaifeng.sqlink.word.Word;

/**
 * LAS Compiler Exception.
 * 
 * @author LiuHaifeng <br>
 *         $Id: SQLCompilerException.java 46 2009-12-04 14:28:03Z LiuHaifeng $
 */
public class SQLCompilerException extends SQLParserException {
	private static final long serialVersionUID = -8112393161495254852L;

	private CompilerError compilerError;

	public SQLCompilerException() {
		super();
		compilerError = new CompilerError();
	}

	public SQLCompilerException(String msg) {
		super(msg);
		compilerError = new CompilerError();
	}

	public SQLCompilerException(Throwable e) {
		super(e);
		compilerError = new CompilerError();
	}

	public SQLCompilerException(String msg, Throwable e) {
		super(msg, e);
		compilerError = new CompilerError();
	}

	public SQLCompilerException(Word w, int errorLevel, int errorCode,
			String msg) {
		super(msg);
		compilerError = new CompilerError(w.getLine(), w.getColumn(),
				errorLevel, errorCode, msg);
	}

	public SQLCompilerException(int line, int column, int errorLevel,
			int errorCode, String msg) {
		super(msg);
		compilerError = new CompilerError(line, column, errorLevel, errorCode,
				msg);
	}

	public SQLCompilerException(int line, int column, int errorLevel,
			int errorCode, String msg, Throwable e) {
		super(msg, e);
		compilerError = new CompilerError(line, column, errorLevel, errorCode,
				msg);
	}

	public CompilerError getCompilerError() {
		return compilerError;
	}
}
