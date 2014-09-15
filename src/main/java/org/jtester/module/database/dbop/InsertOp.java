package org.jtester.module.database.dbop;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.jtester.core.IJTester.DataMap;
import org.jtester.exception.ExceptionWrapper;
import org.jtester.module.database.environment.DBEnvironment;
import org.jtester.module.database.environment.DBEnvironmentFactory;
import org.jtester.module.database.environment.TableMeta;
import org.jtester.module.database.util.DBHelper;

public class InsertOp {
	private String table;

	private DataMap data;

	private DBEnvironment dbEnvironment;

	private TableMeta tableMeta;

	private String quato;

	private InsertOp(String table, DataMap data) {
		this.table = table;
		this.data = data;
		this.dbEnvironment = DBEnvironmentFactory.getCurrentDBEnvironment();
		this.tableMeta = dbEnvironment.getTableMetaData(table);
		this.quato = this.dbEnvironment.getFieldQuato();
	}

	/**
	 * 往数据库中插入数据
	 * 
	 * @param table
	 * @param data
	 * @throws Exception
	 */
	void insert() throws Exception {
		Connection connection = dbEnvironment.connectIfNeeded();

		tableMeta.fillData(this.data, dbEnvironment);
		String sql = getInsertCommandText();
		PreparedStatement statement = connection.prepareStatement(dbEnvironment.preExecute(table, sql));

		statement.clearParameters();
		int index = 1;
		for (String key : data.keySet()) {
			try {
				Object value = getValueByColumn(key);
				if (value instanceof InputStream) {
					InputStream is = (InputStream) value;
					statement.setBinaryStream(index, is, is.available());
				} else {
					statement.setObject(index, value);
				}
				index++;
			} catch (Throwable e) {
				throw new RuntimeException("set column[" + key + "] value error:" + e.getMessage(), e);
			}
		}
		try {
			statement.execute();
		} catch (Exception e) {
			Exception e1 = ExceptionWrapper.getUndeclaredThrowableExceptionCaused(e);
			StringBuffer msg = new StringBuffer();
			msg.append("statement:" + sql);
			msg.append("\ninsert data error, data=\n");
			msg.append(data.toString());
			throw new RuntimeException(msg.toString(), e1);
		} finally {
			DBHelper.closeStatement(statement);
		}
	}

	private Object getValueByColumn(String column) {
		Object value = data.get(column);
		if (!(value instanceof String)) {
			return value;
		}
		String javaType = this.tableMeta.getColumnType(column);
		if (String.class.getName().equals(javaType)) {
			value = this.tableMeta.truncateString(column, (String) value);
		} else {
			value = dbEnvironment.toObjectValue((String) value, javaType);
		}
		return value;
	}

	/**
	 * 构造map的insert sql语句
	 * 
	 * @param table
	 * @param map
	 * @return
	 */
	private String getInsertCommandText() {
		StringBuilder text = new StringBuilder();
		StringBuilder values = new StringBuilder();

		text.append("insert into ").append(table).append("(");
		boolean isFirst = true;
		for (String key : this.data.keySet()) {
			if (isFirst) {
				isFirst = false;
			} else {
				text.append(",");
				values.append(",");
			}
			text.append(this.quato).append(key).append(this.quato);
			values.append("?");
		}

		text.append(") values(").append(values).append(")");
		return text.toString();
	}

	public static void insert(String table, DataMap data) {
		try {
			InsertOp op = new InsertOp(table, data);
			op.insert();
		} catch (Exception e) {
			throw ExceptionWrapper.getUndeclaredThrowableExceptionCaused(e);
		}
	}
}
