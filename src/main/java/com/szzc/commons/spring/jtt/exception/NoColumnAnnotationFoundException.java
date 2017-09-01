package com.szzc.commons.spring.jtt.exception;

import java.lang.reflect.Method;

public class NoColumnAnnotationFoundException extends Exception {

	private static final long serialVersionUID = -2538732748536433343L;

	public NoColumnAnnotationFoundException(String ClassName, Method getter) {
		super(ClassName + "." + getter.getName() + "() should have an @Column annotation.");
	}
}
