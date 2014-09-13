package org.jtester.module.database.environment;

import static org.jtester.module.core.helper.ConfigurationHelper.PROPKEY_DATABASE_TYPE;
import static org.jtester.module.core.helper.ConfigurationHelper.PROPKEY_DATASOURCE_DRIVERCLASSNAME;
import static org.jtester.module.core.helper.ConfigurationHelper.PROPKEY_DATASOURCE_PASSWORD;
import static org.jtester.module.core.helper.ConfigurationHelper.PROPKEY_DATASOURCE_SCHEMAS;
import static org.jtester.module.core.helper.ConfigurationHelper.PROPKEY_DATASOURCE_URL;
import static org.jtester.module.core.helper.ConfigurationHelper.PROPKEY_DATASOURCE_USERNAME;

import static org.jtester.module.database.environment.DBEnvironment.CUSTOMIZED_DATASOURCE_NAME;
import static org.jtester.module.database.environment.DBEnvironment.DEFAULT_DATASOURCE_FROM;
import static org.jtester.module.database.environment.DBEnvironment.DEFAULT_DATASOURCE_NAME;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.jtester.module.core.helper.ConfigurationHelper;
import org.jtester.module.database.environment.impl.DerbyEnvironment;
import org.jtester.module.database.environment.impl.MySqlEnvironment;
import org.jtester.module.database.environment.impl.OracleEnvironment;
import org.jtester.module.database.environment.impl.SqlServerEnvironment;
import org.jtester.module.database.util.DataSourceType;
import org.jtester.utility.ResourceHelper;
import org.jtester.utility.StringHelper;

public final class DBEnvironmentFactory {
	private static Map<String, DBEnvironment> environments = new HashMap<String, DBEnvironment>();

	private static DBEnvironment newInstance(DataSourceType dataSourceType, String dataSourceName, String dataSourceFrom) {
		if (dataSourceType == null) {
			throw new RuntimeException("DatabaseType can't be null.");
		}
		switch (dataSourceType) {
		case MYSQL:
			return new MySqlEnvironment(dataSourceName, dataSourceFrom);
		case ORACLE:
			return new OracleEnvironment(dataSourceName, dataSourceFrom);
		case SQLSERVER:
			return new SqlServerEnvironment(dataSourceName, dataSourceFrom);
		case DERBYDB:
			return new DerbyEnvironment(dataSourceName, dataSourceFrom);
		default:
			throw new RuntimeException("unsupport database type:" + dataSourceType.name());
		}
	}

	/**
	 * 构造自定义的数据库连接识别码
	 * 
	 * @param type
	 * @param driver
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public static DBEnvironment getDBEnvironment(String type, String driver, String url, String username,
			String password) {
		DataSourceType dataSourceType = DataSourceType.databaseType(type);
		String dataSourceFrom = "customized-" + UUID.randomUUID().toString();
		DBEnvironment enviroment = newInstance(dataSourceType, CUSTOMIZED_DATASOURCE_NAME, dataSourceFrom);
		environments.put(DEFAULT_DATASOURCE_NAME + "=" + dataSourceFrom, enviroment);

		if (StringHelper.isBlankOrNull(driver)) {
			driver = ConfigurationHelper.databaseDriver();
		}
		if (StringHelper.isBlankOrNull(url)) {
			url = ConfigurationHelper.databaseUrl();
		}
		if (StringHelper.isBlankOrNull(username)) {
			username = ConfigurationHelper.databaseUserName();
		}
		if (StringHelper.isBlankOrNull(password)) {
			password = ConfigurationHelper.databasePassword();
		}
		enviroment.setDataSource(driver, url, "", username, password);

		return enviroment;
	}

	final static String NO_VALID_VALUE_MESSAGE = "can't find valid value of key[%s] in file[%s]!";

	/**
	 * 获取默认的数据库连接识别码
	 * 
	 * @return
	 */
	public static DBEnvironment getDBEnvironment() {
		DBEnvironment enviroment = getDBEnvironment(DEFAULT_DATASOURCE_NAME, DEFAULT_DATASOURCE_FROM);
		return enviroment;
	}

	/**
	 * 从jtester配置中取指定的数据源
	 * 
	 * @param dataSourceName
	 * @return
	 */
	public static DBEnvironment getDBEnvironment(String dataSourceName) {
		if (StringHelper.isBlankOrNull(dataSourceName)) {
			dataSourceName = DEFAULT_DATASOURCE_NAME;
		}
		DBEnvironment enviroment = getDBEnvironment(dataSourceName, DEFAULT_DATASOURCE_FROM);
		return enviroment;
	}

	/**
	 * 从文件中获取指定的数据库连接识别码
	 * 
	 * @param dataSourceName
	 * @param dataSourceFrom
	 * @return
	 */
	public static DBEnvironment getDBEnvironment(String dataSourceName, String dataSourceFrom) {
		dataSourceName = StringHelper.isBlankOrNull(dataSourceName) ? DEFAULT_DATASOURCE_NAME : dataSourceName;
		dataSourceFrom = StringHelper.isBlankOrNull(dataSourceFrom) ? DEFAULT_DATASOURCE_FROM : dataSourceFrom;

		DBEnvironment enviroment = environments.get(dataSourceName + "=" + dataSourceFrom);
		if (enviroment == null) {
			Properties props = null;
			if (StringHelper.isBlankOrNull(dataSourceFrom) == false
					&& DEFAULT_DATASOURCE_FROM.equalsIgnoreCase(dataSourceFrom) == false) {
				props = ResourceHelper.loadPropertiesFrom(dataSourceFrom);
			}
			String typeProperty = ConfigurationHelper.getString(props,
					getMergeKey(dataSourceName, PROPKEY_DATABASE_TYPE));
			DataSourceType dataSourceType = DataSourceType.databaseType(typeProperty);

			enviroment = newInstance(dataSourceType, dataSourceName, dataSourceFrom);

			environments.put(dataSourceName + "=" + dataSourceFrom, enviroment);

			String driver = ConfigurationHelper.getString(props,
					getMergeKey(dataSourceName, PROPKEY_DATASOURCE_DRIVERCLASSNAME));
			String url = ConfigurationHelper.getString(props, getMergeKey(dataSourceName, PROPKEY_DATASOURCE_URL));
			String user = ConfigurationHelper
					.getString(props, getMergeKey(dataSourceName, PROPKEY_DATASOURCE_USERNAME));
			if (StringHelper.isBlankOrNull(driver) || StringHelper.isBlankOrNull(url)
					|| StringHelper.isBlankOrNull(user)) {
				throw new RuntimeException(String.format(NO_VALID_VALUE_MESSAGE, dataSourceName + "."
						+ PROPKEY_DATASOURCE_USERNAME, dataSourceFrom));
			}
			String pass = ConfigurationHelper
					.getString(props, getMergeKey(dataSourceName, PROPKEY_DATASOURCE_PASSWORD));
			if (pass == null) {
				pass = "";
			}
			String schemas = ConfigurationHelper.getString(props,
					getMergeKey(dataSourceName, PROPKEY_DATASOURCE_SCHEMAS), "");
			schemas = schemas == null ? "" : schemas;

			enviroment.setDataSource(driver, url, schemas, user, pass);
		}

		return enviroment;
	}

	private static String getMergeKey(String dataSourceName, String key) {
		if (StringHelper.isBlankOrNull(dataSourceName) || DEFAULT_DATASOURCE_NAME.equalsIgnoreCase(dataSourceName)) {
			return key;
		} else {
			return dataSourceName + "." + key;
		}
	}

	/**
	 * 提交当前数据源的操作，并且关闭数据源
	 */
	public static void commitCurrentDBEnvironment() {
		if (currDBEnvironment == null) {
			return;
		}
		currDBEnvironment.close();
	}

	/**
	 * 当前正在使用的数据库类型
	 */
	private static DBEnvironment currDBEnvironment = null;

	/**
	 * 获取当前的数据库处理环境
	 * 
	 * @return
	 */
	public static DBEnvironment getCurrentDBEnvironment() {
		if (currDBEnvironment == null) {
			currDBEnvironment = getDBEnvironment();
		}
		return currDBEnvironment;
	}

	/**
	 * 切换数据库环境<br>
	 * 先关闭上一个数据库连接，再设置当前数据库连接
	 * 
	 * @param DataSourceIdentify
	 * @throws SQLException
	 */
	public static void changeDBEnvironment(DBEnvironment environment) {
		if (currDBEnvironment != null && currDBEnvironment.equals(environment) == false) {
			currDBEnvironment.close();
		}
		currDBEnvironment = environment;
	}

	/**
	 * 切换数据源<br>
	 * 先关闭上一个数据库连接，再设置当前数据库连接
	 * 
	 * @param dataSourceName
	 *            jTester中配置的数据源名称
	 */
	public static void changeDBEnvironment(String dataSourceName) {
		if (StringHelper.isBlankOrNull(dataSourceName)) {
			dataSourceName = DEFAULT_DATASOURCE_NAME;
		}
		DBEnvironment specEnvironment = DBEnvironmentFactory.getDBEnvironment(dataSourceName);
		DBEnvironmentFactory.changeDBEnvironment(specEnvironment);
	}

	/**
	 * 结束所有可能的事务
	 * 
	 * @throws SQLException
	 */
	public static void endTransactional() {
		StringBuilder err = new StringBuilder();
		for (DBEnvironment environment : environments.values()) {
			try {
				Connection conn = environment.getConnection();
				if (conn != null && conn.isClosed() == false) {
					environment.rollback();
					environment.close();
				}
			} catch (Throwable e) {
				err.append(StringHelper.exceptionTrace(e));
			}
		}

		String msg = err.toString();
		if ("".equalsIgnoreCase(msg.trim()) == false) {
			throw new RuntimeException(msg);
		}
	}
}
