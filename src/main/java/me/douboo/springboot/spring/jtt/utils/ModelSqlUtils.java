package me.douboo.springboot.spring.jtt.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.util.StringUtils;

import me.douboo.springboot.spring.jtt.annotation.NameColumn;
import me.douboo.springboot.spring.jtt.annotation.Naming;
import me.douboo.springboot.spring.jtt.exception.MethodInvokeException;
import me.douboo.springboot.spring.jtt.exception.NoColumnAnnotationFoundException;
import me.douboo.springboot.spring.jtt.exception.NoDefinedGetterException;
import me.douboo.springboot.spring.jtt.exception.NoIdAnnotationFoundException;
import me.douboo.springboot.spring.jtt.model.SqlParamsPairs;

/**
 * Turn model to sql
 * 
 * @author Administrator
 *
 */
public class ModelSqlUtils {

	private static String quotes = "`";
	private static Logger logger = LoggerFactory.getLogger(ModelSqlUtils.class);

	/**
	 * 从po对象中分析出insert语句
	 * 
	 * @param po
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static <T> SqlParamsPairs getInsertFromObject(T po) {

		// 用来存放insert语句
		StringBuffer insertSql = new StringBuffer();
		// 用来存放?号的语句
		StringBuffer paramsSql = new StringBuffer();

		// 用来存放参数值
		List<Object> params = new ArrayList<Object>();

		// 分析表名
		String tableName = getTableName(po.getClass());

		insertSql.append("insert into " + tableName + " (");

		// 计数器
		int count = 0;

		// 分析列
		Field[] fields = po.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];

			if ("serialVersionUID".equals(f.getName())) {
				continue;
			}

			// 获取具体参数值
			Method getter = getGetter(po.getClass(), f);

			if (getter == null) {
				continue;
			}

			Object value;
			try {
				value = getter.invoke(po);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new MethodInvokeException(po.getClass().getName(), getter);
			}
			if (value == null) {
				// 如果参数值是null就直接跳过（不允许覆盖为null值，规范要求更新的每个字段都要有值，没有值就是空字符串）
				continue;
			}

			Transient tranAnno = getter.getAnnotation(Transient.class);
			if (tranAnno != null) {
				// 如果有 Transient 标记直接跳过
				continue;
			}

			// 获取字段名
			String columnName = getColumnNameFromGetter(getter, f);

			if (count != 0) {
				insertSql.append(",");
			}
			insertSql.append(quotes).append(columnName).append(quotes);

			if (count != 0) {
				paramsSql.append(",");
			}
			paramsSql.append("?");

			params.add(value);
			count++;
		}

		insertSql.append(") values (");
		insertSql.append(paramsSql + ")");

		SqlParamsPairs sqlAndParams = new SqlParamsPairs(insertSql.toString(), params.toArray());
		logger.debug(sqlAndParams.toString());
		return sqlAndParams;

	}

	/**
	 * 获取属性的getter方法
	 * 
	 * @param clazz
	 * @param f
	 * @return
	 */
	private static <T> Method getGetter(Class<T> clazz, Field f) {
		String getterName = "get" + CamelNameUtils.capitalize(f.getName());
		Method getter = null;
		try {
			getter = clazz.getMethod(getterName);
		} catch (Exception e) {
			logger.debug(getterName + " doesn't exist!", e);
		}
		return getter;
	}

	/**
	 * 从po类获取表名
	 * 
	 * @param po
	 * @return
	 */
	public static <T> String getTableName(Class<T> clazz) {

		Table tableAnno = clazz.getAnnotation(Table.class);
		if (tableAnno != null) {
			if (!StringUtils.isEmpty(tableAnno.catalog())) {
				return tableAnno.catalog() + "." + tableAnno.name();
			}
			return tableAnno.name();
		}
		// if Table annotation is null
		String className = clazz.getName();
		return CamelNameUtils.camel2underscore(className.substring(className.lastIndexOf(".") + 1));
	}

	/**
	 * 从对象中获取update语句
	 * 
	 * @param po
	 * @return
	 * @throws Exception
	 */
	public static SqlParamsPairs getUpdateFromObject(Object po) {

		// 用来存放insert语句
		StringBuffer updateSql = new StringBuffer();

		// 用来存放where语句
		StringBuffer whereSql = new StringBuffer();

		// 用来存放参数值
		List<Object> params = new ArrayList<Object>();

		// 用来存储id
		Object idValue = null;

		// 分析表名
		String tableName = getTableName(po.getClass());

		updateSql.append("update " + tableName + " set");

		// 分析列
		Field[] fields = po.getClass().getDeclaredFields();

		// 用于计数
		int count = 0;
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];

			// 获取具体参数值
			Method getter = getGetter(po.getClass(), f);

			if (getter == null) {
				continue;
			}

			Object value;
			try {
				value = getter.invoke(po);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new MethodInvokeException(po.getClass().getName(), getter);
			}
			/*
			 * if (value == null) { //
			 * 如果参数值是null就直接跳过（不允许覆盖为null值，规范要求更新的每个字段都要有值，没有值就是空字符串） continue; }
			 */

			Transient tranAnno = getter.getAnnotation(Transient.class);
			if (tranAnno != null) {
				// 如果有 Transient 标记直接跳过
				continue;
			}

			// 获取字段名
			String columnName = getColumnNameFromGetter(getter, f);

			// 看看是不是主键
			Id idAnno = getter.getAnnotation(Id.class);
			if (idAnno != null) {
				// 如果是主键
				whereSql.append(columnName + " = ?");
				idValue = value;
				continue;
			}

			// 如果是普通列
			params.add(value);

			if (count != 0) {
				updateSql.append(",");
			}
			updateSql.append(" " + columnName + " = ?");

			count++;
		}

		updateSql.append(" where ");
		updateSql.append(whereSql);
		params.add(idValue);

		SqlParamsPairs sqlAndParams = new SqlParamsPairs(updateSql.toString(), params.toArray());
		logger.debug(sqlAndParams.toString());

		return sqlAndParams;

	}

	/**
	 * 按字段更新某个对象
	 * 
	 * @author luheng
	 * @param po
	 * @param cols
	 * @return
	 * @throws Exception
	 */
	public static SqlParamsPairs getMergeFromObject(Object po, String[] cols) {

		// 用来存放insert语句
		StringBuffer updateSql = new StringBuffer();

		// 用来存放where语句
		StringBuffer whereSql = new StringBuffer();

		// 用来存放参数值
		List<Object> params = new ArrayList<Object>();

		// 用来存储id
		Object idValue = null;

		// 分析表名
		String tableName = getTableName(po.getClass());

		updateSql.append("update " + tableName + " set");

		// 分析列
		Field[] fields = po.getClass().getDeclaredFields();

		// 用于计数
		int count = 0;
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];

			// 获取具体参数值
			Method getter = getGetter(po.getClass(), f);

			if (getter == null) {
				continue;
			}

			Object value;
			try {
				value = getter.invoke(po);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new MethodInvokeException(po.getClass().getName(), getter);
			}
			if (value == null) {
				// 如果参数值是null就直接跳过（不允许覆盖为null值，规范要求更新的每个字段都要有值，没有值就是空字符串）
				continue;
			}

			Transient tranAnno = getter.getAnnotation(Transient.class);
			if (tranAnno != null) {
				// 如果有 Transient 标记直接跳过
				continue;
			}

			// 获取字段名
			String columnName = getColumnNameFromGetter(getter, f);

			// 看看是不是主键
			Id idAnno = getter.getAnnotation(Id.class);
			if (idAnno != null) {
				// 如果是主键
				whereSql.append(columnName + " = ?");
				idValue = value;
				continue;
			}

			// 如果是普通列并且是指定列
			if (Arrays.asList(cols).contains(columnName)) {
				params.add(value);

				if (count != 0) {
					updateSql.append(",");
				}
				// 对mysql字段名做特殊处理，加上·
				updateSql.append(" `" + columnName + "` = ?");

				count++;
			}
		}

		updateSql.append(" where ");
		updateSql.append(whereSql);
		params.add(idValue);

		SqlParamsPairs sqlAndParams = new SqlParamsPairs(updateSql.toString(), params.toArray());
		logger.debug(sqlAndParams.toString());

		return sqlAndParams;
	}

	/**
	 * 从对象中获取delete语句
	 * 
	 * @param po
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static SqlParamsPairs getDeleteFromObject(Object po) {

		// 用来存放insert语句
		StringBuffer deleteSql = new StringBuffer();

		// 用来存储id
		Object idValue = null;

		// 分析表名
		String tableName = getTableName(po.getClass());

		deleteSql.append("delete from " + tableName + " where ");

		Class clazz = po.getClass();
		// 分析列
		Field[] fields = clazz.getDeclaredFields();

		// 用于寻找id字段
		Id idAnno = null;
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];

			// 找id字段
			Method getter = getGetter(clazz, f);

			if (getter == null) {
				// 没有get方法直接跳过
				continue;
			}

			// 看是不是主键
			idAnno = getter.getAnnotation(Id.class);
			if (idAnno == null) {
				continue;
			}

			// 看有没有定义column
			String columnName = getColumnNameFromGetter(getter, f);

			deleteSql.append(columnName + " = ?");

			try {
				idValue = getter.invoke(po, new Object[] {});
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new MethodInvokeException(po.getClass().getName(), getter);
			}

			break;
		}

		// 全部遍历完如果找不到主键就抛异常
		if (idAnno == null) {
			throw new NoIdAnnotationFoundException(clazz);
		}

		SqlParamsPairs sqlAndParams = new SqlParamsPairs(deleteSql.toString(), new Object[] { idValue });
		logger.debug(sqlAndParams.toString());

		return sqlAndParams;

	}

	/**
	 * 获取根据主键查对象的sql和参数
	 * 
	 * @param po
	 * @param id
	 * @return
	 * @throws NoIdAnnotationFoundException
	 * @throws NoColumnAnnotationFoundException
	 * @throws NoDefinedGetterException
	 * @throws @throws
	 *             Exception
	 */
	public static <T> SqlParamsPairs getGetFromObject(Class<T> clazz, Object id)
			throws NoIdAnnotationFoundException, NoColumnAnnotationFoundException {

		// 用来存放get语句
		StringBuffer getSql = new StringBuffer();

		// 分析表名
		String tableName = getTableName(clazz);

		getSql.append("select * from " + tableName + " where ");

		// 分析列
		Field[] fields = clazz.getDeclaredFields();
		Id idAnno = null;
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];

			// 找id字段
			Method getter = getGetter(clazz, f);

			if (getter == null) {
				// 没有get方法直接跳过
				continue;
			}

			// 看是不是主键
			idAnno = getter.getAnnotation(Id.class);
			if (idAnno == null) {
				continue;
			}

			// get column name
			String columnName = getColumnNameFromGetter(getter, f);

			getSql.append(columnName + " = ?");

			break;
		}

		// 全部遍历完如果找不到主键就抛异常
		if (idAnno == null) {
			throw new NoIdAnnotationFoundException(clazz);
		}

		SqlParamsPairs sqlAndParams = new SqlParamsPairs(getSql.toString(), new Object[] { id });
		logger.debug(sqlAndParams.toString());

		return sqlAndParams;
	}

	/**
	 * use getter to guess column name, if there is annotation then use annotation
	 * value, if not then guess from field name
	 * 
	 * @param getter
	 * @param clazz
	 * @param f
	 * @return
	 * @throws NoColumnAnnotationFoundException
	 */
	private static String getColumnNameFromGetter(Method getter, Field f) {
		String columnName = "";
		Column columnAnno = getter.getAnnotation(Column.class);
		if (columnAnno != null) {
			// 如果是列注解就读取name属性
			columnName = columnAnno.name();
		}

		if (columnName == null || "".equals(columnName)) {
			// 如果没有列注解就用命名方式去猜
			columnName = f.getName();

			// 优先解析方法
			NameColumn nameColumnMethod = getter.getAnnotation(NameColumn.class);
			if (nameColumnMethod != null) {
				Naming naming = nameColumnMethod.naming();
				if (Naming.CAMEL == naming) {
					columnName = CamelNameUtils.underscore2camel(columnName);
				} else if (Naming.UNDERLINE == naming) {
					columnName = CamelNameUtils.camel2underscore(columnName);
				}
			} else {
				// 再解析类是否有命名规范
				NameColumn nameColumnType = getter.getDeclaringClass().getAnnotation(NameColumn.class);
				if (nameColumnType != null) {
					Naming naming = nameColumnType.naming();
					if (Naming.CAMEL == naming) {
						columnName = CamelNameUtils.underscore2camel(columnName);
					} else if (Naming.UNDERLINE == naming) {
						columnName = CamelNameUtils.camel2underscore(columnName);
					}
				} else {
					// 默认使用下划线写法
					columnName = CamelNameUtils.camel2underscore(columnName);
				}
			}

		}
		return columnName;
	}

	public static void setDriver(DatabaseDriver driver) {
		if (driver == DatabaseDriver.SQLSERVER) {
			quotes = "";
		}
	}

}
