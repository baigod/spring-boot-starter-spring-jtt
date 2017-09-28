package me.douboo.springboot.spring.jtt.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import me.douboo.springboot.spring.jtt.JdbcTemplateProxy;
import me.douboo.springboot.spring.jtt.JdbcTemplateTool;
import me.douboo.springboot.spring.jtt.dao.BaseDao;
import me.douboo.springboot.spring.jtt.exception.DBException;

/**
 * 持久层抽象
 * 
 * @author luheng
 * @version v01.00.00 $Revision$
 * @date 2015年3月18日
 * @time 下午3:39:00
 */
public class BaseDaoImpl implements BaseDao {

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Autowired
	protected JdbcTemplateTool jdbcTemplateTool;
	
	@Autowired
	protected JdbcTemplateProxy jdbcTemplateProxy;

	/**
	 * 保存
	 * 
	 * @param o 对象
	 */
	@Override
	public void save(Object o) {
		try {
			this.jdbcTemplateTool.save(o);
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	/**
	 * 根据id获取单个
	 * 
	 * @param id id
	 * @param clazz 反编译对象
	 * @return 泛型对象
	 */
	@Override
	public <T> T get(Object id, Class<?> clazz) {
		try {
			return this.jdbcTemplateTool.get(clazz, id);
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	/**
	 * 更新实体
	 * 
	 * @param o 对象
	 */
	@Override
	public void update(Object o) {
		try {
			this.jdbcTemplateTool.update(o);
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	/**
	 * 按字段更新实体
	 */
	@Override
	public void merge(Object o, String... cols) {
		try {
			this.jdbcTemplateTool.merge(o, cols);
		} catch (Exception e) {
			throw new DBException(e);
		}

	}

	/**
	 * 删除实体
	 * 
	 * @param o 对象
	 */
	@Override
	public void delete(Object o) {
		try {
			this.jdbcTemplateTool.delete(o);
		} catch (Exception e) {
			throw new DBException(e);
		}
	}
	
	


}
