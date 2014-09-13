package org.jtester.module.tracer.jdbc;

import org.jtester.module.tracer.TracerBeanManager;

/**
 * jdbc的跟踪信息
 * 
 * @author darui.wudr
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class JdbcTracerEvent {
	private Class beanClazz;
	private String sql;

	public JdbcTracerEvent(Class beanClazz, String sql) {
		this.beanClazz = beanClazz;
		this.sql = sql;
	}

	public Class getBeanClazz() {
		return beanClazz;
	}

	public String getSql() {
		return sql;
	}

	public String toHtmlString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<tr>");
		if (this.beanClazz == null) {
			buffer.append("<td>bean</td>");
		} else {
			String beanName = TracerBeanManager.getBeanName(beanClazz);
			buffer.append(String.format("<td title='%s'>%s</td>", beanClazz.getName(), beanName));
		}
		buffer.append("<td>");
		buffer.append(TracerSQLUtility.highlightSql(sql));
		buffer.append("</td>");
		buffer.append("</tr>");
		return buffer.toString();
	}
}
