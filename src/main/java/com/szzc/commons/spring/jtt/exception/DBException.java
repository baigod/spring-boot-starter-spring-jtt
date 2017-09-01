package com.szzc.commons.spring.jtt.exception;

public class DBException extends RuntimeException {

	public static String desc = "数据异常，请联系管理员!";

	private static final long serialVersionUID = 1L;

	private String errorCode;

	public DBException() {
		super(desc);
	}

	public DBException(Exception e) {
		super(e);
	}

	public DBException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public DBException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBException(String message) {
		super(message);
	}

	public DBException(Throwable cause) {
		super(cause);
	}

	public String getErrorCode() {
		return errorCode;
	}

}
