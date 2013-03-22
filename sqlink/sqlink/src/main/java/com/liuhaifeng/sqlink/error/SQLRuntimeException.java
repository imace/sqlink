package com.liuhaifeng.sqlink.error;

public class SQLRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 6243423555326632504L;

	public SQLRuntimeException() {
		super();
	}

	public SQLRuntimeException(String msg) {
		super(msg);
	}

	public SQLRuntimeException(Throwable e) {
		super(e);
	}

	public SQLRuntimeException(String msg, Throwable e) {
		super(msg, e);
	}

}
