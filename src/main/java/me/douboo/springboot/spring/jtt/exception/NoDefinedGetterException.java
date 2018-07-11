package me.douboo.springboot.spring.jtt.exception;

public class NoDefinedGetterException extends RuntimeException {

	private static final long serialVersionUID = -1213525052763444089L;
	private String fieldName;

	public NoDefinedGetterException(String fieldName) {
		super(fieldName + " should have an getter method.");
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
