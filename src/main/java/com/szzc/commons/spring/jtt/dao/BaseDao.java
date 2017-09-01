package com.szzc.commons.spring.jtt.dao;

/**
 * 持久层抽象
 * 
 * @author luheng
 * @version v01.00.00 $Revision$
 * @date 2015年3月18日
 * @time 下午3:39:00
 */
public interface BaseDao {

	/**
	 * 保存
	 * 
	 * @param o 對象
	 */
	void save(Object o);

	/**
	 * 根据id获取单个
	 * 
	 * @param id
	 * @param clazz
	 * @return 泛型對象
	 */
	<T> T get(Object id, Class<?> clazz);

	/**
	 * 更新实体
	 * 
	 * @param o 对象
	 */
	void update(Object o);

	/**
	 * 按字段更新
	 * 
	 * @author luheng
	 * @param po 实体
	 * @param cols 数据库字段名
	 */
	void merge(Object po, String... cols);

	/**
	 * 删除实体
	 * 
	 * @param o 對象
	 */
	void delete(Object o);

}
