package org.jtester.module.database.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.jtester.core.IJTester.DataMap;
import org.jtester.exception.DbFitException;
import org.jtester.exception.ExceptionWrapper;
import org.jtester.fit.util.SymbolUtil;
import org.jtester.module.database.environment.DBEnvironment;
import org.jtester.module.database.environment.DBEnvironmentFactory;
import org.jtester.utility.ResourceHelper;

/**
 * sql 执行器
 * 
 * @author darui.wudr
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SqlRunner {

	public static void commit() {
		try {
			DBEnvironment environment = DBEnvironmentFactory.getCurrentDBEnvironment();
			environment.connectIfNeeded();
			environment.commit();
		} catch (Exception e) {
			throw ExceptionWrapper.getUndeclaredThrowableExceptionCaused(e);
		}
	}

	public static void rollback() {
		try {
			DBEnvironment environment = DBEnvironmentFactory.getCurrentDBEnvironment();
			environment.connectIfNeeded();
			environment.rollback();
		} catch (Exception e) {
			throw ExceptionWrapper.getUndeclaredThrowableExceptionCaused(e);
		}
	}

	/**
	 * 执行sql语句
	 * 
	 * @param sql
	 * @throws SQLException
	 */
	public static void execute(String sql) {
		DBEnvironment environment = DBEnvironmentFactory.getCurrentDBEnvironment();
		environment.connectIfNeeded();
		PreparedStatement st = null;
		try {
			st = environment.createStatementWithBoundFixtureSymbols(sql);
			st.execute();
		} catch (Throwable e) {
			throw ExceptionWrapper.getUndeclaredThrowableExceptionCaused(e);
		} finally {
			DBHelper.closeStatement(st);
		}
	}

	/**
	 * 设置变量，执行sql文件
	 * 
	 * @param symbols
	 * @param fileName
	 * @throws SQLException
	 * @throws FileNotFoundException
	 */
	public static void executeFromFile(Map<String, ?> symbols, String fileName) throws Exception {
		SymbolUtil.setSymbol(symbols);
		executeFromFile(fileName);
	}

	/**
	 * 执行sql文件<br>
	 * 默认从classpath中读取<br>
	 * classpath:前缀开头，表示从classpath中读取<br>
	 * file:前缀开头，表示从文件系统中读取<br>
	 * 
	 * @param fileName
	 * @throws SQLException
	 * @throws FileNotFoundException
	 */
	public static void executeFromFile(String fileName) throws Exception {
		DBEnvironment environment = DBEnvironmentFactory.getCurrentDBEnvironment();
		environment.connectIfNeeded();

		String sqls = ResourceHelper.readFromFile(fileName);
		String[] statements = DBHelper.parseSQL(sqls);
		for (String statment : statements) {
			PreparedStatement st = null;
			try {
				st = environment.createStatementWithBoundFixtureSymbols(statment);
				st.execute();
			} catch (Throwable e) {
				throw new DbFitException("there are some error when execute sql file [" + fileName + "]", e);
			} finally {
				DBHelper.closeStatement(st);
			}
		}
	}

	/**
	 * 执行sql文件流
	 * 
	 * @param is
	 * @throws Exception
	 */
	public static void executeFromStream(InputStream is) throws Exception {
		DBEnvironment environment = DBEnvironmentFactory.getCurrentDBEnvironment();
		environment.connectIfNeeded();

		String sqls = ResourceHelper.readFromStream(is);
		String[] statements = DBHelper.parseSQL(sqls);
		for (String statment : statements) {
			PreparedStatement st = null;
			try {
				st = environment.createStatementWithBoundFixtureSymbols(statment);
				st.execute();
			} catch (Throwable e) {
				throw new DbFitException("there are some error when execute sql file.", e);
			} finally {
				DBHelper.closeStatement(st);
			}
		}
	}

	/**
	 * 执行sql文件<br>
	 * 
	 * @see executeFromFile
	 * 
	 * @param clazz
	 *            SQL文件所在的classpath
	 * @param fileName
	 * @throws SQLException
	 * @throws FileNotFoundException
	 */
	public static void executeFromFile(Class clazz, String fileName) {
		DBEnvironment environment = DBEnvironmentFactory.getCurrentDBEnvironment();
		environment.connectIfNeeded();

		String sqls;
		try {
			sqls = ResourceHelper.readFromFile(clazz, fileName);
		} catch (FileNotFoundException e1) {
			throw new RuntimeException(e1);
		}

		String[] statements = DBHelper.parseSQL(sqls);
		for (String statment : statements) {
			PreparedStatement st = null;
			try {
				st = environment.createStatementWithBoundFixtureSymbols(statment);
				st.execute();
			} catch (Throwable e) {
				throw new DbFitException("there are some error when execute sql file [" + fileName + "]", e);
			} finally {
				DBHelper.closeStatement(st);
			}
		}
	}

	/**
	 * 根据sql查询数据，如果result是Map.class则返回Map类型<br>
	 * 如果是PoJo，则根据camel name命名方式初始化result
	 * 
	 * @param <T>
	 * @param sql
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	public static <T> T query(String sql, Class<T> claz) {
		DBEnvironment environment = DBEnvironmentFactory.getCurrentDBEnvironment();
		environment.connectIfNeeded();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = environment.createStatementWithBoundFixtureSymbols(sql);
			rs = st.executeQuery();
			if (rs.next() == false) {
				throw new RuntimeException("there are no result for statement:" + sql);
			}
			ResultSetMetaData rsmd = rs.getMetaData();
			if (Map.class.isAssignableFrom(claz)) {
				Map value = DBHelper.getMapFromResult(rs, rsmd, false);
				return (T) value;
			} else {
				T pojo = DBHelper.getPoJoFromResult(rs, rsmd, claz);
				return pojo;
			}
		} catch (Throwable e) {
			throw ExceptionWrapper.getUndeclaredThrowableExceptionCaused(e);
		} finally {
			DBHelper.closeResultSet(rs);
			DBHelper.closeStatement(st);
		}
	}

	/**
	 * 执行sql，返回查询数据列表，如果result是Map.class则返回Map列表<br>
	 * 如果是PoJo，则根据camel name命名方式初始化result，返回PoJo列表
	 * 
	 * @param <T>
	 * @param sql
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	public static <T> List<T> queryList(String sql, Class<T> clazz) {
		DBEnvironment environment = DBEnvironmentFactory.getCurrentDBEnvironment();
		environment.connectIfNeeded();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = environment.createStatementWithBoundFixtureSymbols(sql);
			rs = st.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();
			if (Map.class.isAssignableFrom(clazz)) {
				List<Map> maps = DBHelper.getListMapFromResult(rs, rsmd, false);
				return (List<T>) maps;
			} else {
				List<T> list = DBHelper.getListPoJoFromResult(rs, rsmd, clazz);
				return list;
			}
		} catch (Throwable e) {
			throw ExceptionWrapper.getUndeclaredThrowableExceptionCaused(e);
		} finally {
			DBHelper.closeResultSet(rs);
			DBHelper.closeStatement(st);
		}
	}

	public static <T> List<T> queryMapList(String sql) {
		DBEnvironment environment = DBEnvironmentFactory.getCurrentDBEnvironment();
		environment.connectIfNeeded();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = environment.createStatementWithBoundFixtureSymbols(sql);
			rs = st.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();
			List<Map> maps = DBHelper.getListMapFromResult(rs, rsmd, false);
			return (List<T>) maps;
		} catch (Throwable e) {
			throw ExceptionWrapper.getUndeclaredThrowableExceptionCaused(e);
		} finally {
			DBHelper.closeResultSet(rs);
			DBHelper.closeStatement(st);
		}
	}

	public static <T> List<T> queryMapList(String sql, DataMap where) {
		DBEnvironment environment = DBEnvironmentFactory.getCurrentDBEnvironment();
		environment.connectIfNeeded();
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = environment.createStatementWithBoundFixtureSymbols(sql);
			int index = 1;
			for (String key : where.keySet()) {
				try {
					Object value = where.get(key);
					if (value instanceof InputStream) {
						InputStream is = (InputStream) value;
						st.setBinaryStream(index, is, is.available());
					} else {
						st.setObject(index, value);
					}
					index++;
				} catch (Throwable e) {
					throw new RuntimeException("set column[" + key + "] value error:" + e.getMessage(), e);
				}
			}

			rs = st.executeQuery();

			ResultSetMetaData rsmd = rs.getMetaData();
			List<Map> maps = DBHelper.getListMapFromResult(rs, rsmd, false);
			return (List<T>) maps;
		} catch (Throwable e) {
			throw ExceptionWrapper.getUndeclaredThrowableExceptionCaused(e);
		} finally {
			DBHelper.closeResultSet(rs);
			DBHelper.closeStatement(st);
		}
	}

	public static <T> Map<String, Object> queryMap(String sql) {
		List<Map> list = queryMapList(sql);
		if (list.size() == 0) {
			return null;
		} else if (list.size() > 1) {
			throw new RuntimeException("to many result, u want to query one RowSet.");
		} else {
			return list.get(0);
		}
	}
}
