package me.douboo.springboot.spring.jtt.exception;

public class NoIdAnnotationFoundException extends RuntimeException {

	private static final long serialVersionUID = -1295140057846546387L;

	@SuppressWarnings("rawtypes")
	public NoIdAnnotationFoundException(Class clazz) {
		super(clazz + " doesn't have an id field, please make sure the getters of " + clazz
				+ " contain a column with an @id annotation. Note: remember to add annotation above getter instead of attribute itself.");
	}
}
