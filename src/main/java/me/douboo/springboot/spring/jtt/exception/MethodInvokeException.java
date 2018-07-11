package me.douboo.springboot.spring.jtt.exception;

import java.lang.reflect.Method;

public class MethodInvokeException extends RuntimeException {

	private static final long serialVersionUID = -2538732748536433343L;

	public MethodInvokeException(String ClassName, Method getter) {
		super(" should not invoke " + ClassName + "." + getter.getName() + "().");
	}
}
