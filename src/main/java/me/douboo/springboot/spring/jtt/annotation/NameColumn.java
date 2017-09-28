package me.douboo.springboot.spring.jtt.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 命名规范
 * 
 * @author Luheng
 *
 */
@Retention(RUNTIME)
@Target({ METHOD, TYPE })
public @interface NameColumn {

	Naming naming() default Naming.UNDERLINE;

	Naming value() default Naming.UNDERLINE;
}