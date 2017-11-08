package me.douboo.springboot.spring.jtt.utils;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



public class IdUtils {
	
	/**
	 * 根据注解获取自增主键字段名
	 * 如果没找到就返回空字符串
	 * @param po
	 * @return increamentIdFieldName
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public static String getAutoGeneratedId(Object po) throws SecurityException, NoSuchMethodException{
		String autoGeneratedId = "";
		//根据注解获取自增主键字段名
		Field[] allFields = po.getClass().getDeclaredFields();
		for(Field f:allFields){
			
			if("serialVersionUID".equals(f.getName())){
				continue;
			}
			
			//获取getter方法
			String getterName = "get" + CamelNameUtils.capitalize(f.getName());
			Method getter = po.getClass().getDeclaredMethod(getterName);
			
			Id idAnno = getter.getAnnotation(Id.class);
			if(idAnno == null){
				continue;
			}
			GeneratedValue generatedValueAnno = getter.getAnnotation(GeneratedValue.class);
			if(generatedValueAnno == null){
				continue;
			}
			
			if(GenerationType.IDENTITY == generatedValueAnno.strategy() || GenerationType.TABLE == generatedValueAnno.strategy()){
				autoGeneratedId = f.getName();
				break;
			}
		}
		return autoGeneratedId;
	}
	
	/**
	 * 将自增id的值设置回去
	 * @param po
	 * @param autoGeneratedId
	 * @param idValue
	 * @throws Exception
	 * @throws NoSuchMethodException
	 */
	public static void setAutoIncreamentIdValue(Object po,String autoGeneratedId,Object idValue) throws Exception, NoSuchMethodException{
		String setterName = "set" + CamelNameUtils.capitalize(autoGeneratedId);
		Method setter = po.getClass().getDeclaredMethod(setterName, idValue.getClass());
		setter.invoke(po, idValue);
	}
	
}
