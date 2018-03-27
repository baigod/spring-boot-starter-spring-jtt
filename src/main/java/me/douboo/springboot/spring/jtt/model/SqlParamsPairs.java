package me.douboo.springboot.spring.jtt.model;

public class SqlParamsPairs {

	private String sql;

	private Object[] params;

	public SqlParamsPairs() {

	}

	public SqlParamsPairs(String sql, Object[] params) {
		this.sql = sql;
		this.params = params;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < params.length; i++) {
			Object p = params[i];
			if (p instanceof String) {
				sb.append("'").append(p).append("'");
			} else {
				sb.append(p);
			}
			if (i < params.length - 1) {
				sb.append(" , ");
			}
		}
		sb.append("]");
		return "SqlParamsPairs [sql=" + sql + ", params=" + sb + "]";
	}

}
