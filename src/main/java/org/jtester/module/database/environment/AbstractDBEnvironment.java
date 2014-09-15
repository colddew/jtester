package org.jtester.module.database.environment;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.jtester.bytecode.reflector.helper.ClazzConst;
import org.jtester.bytecode.reflector.helper.ClazzHelper;
import org.jtester.core.TestedObject;
import org.jtester.core.context.DbFitContext;
import org.jtester.core.context.DbFitContext.RunIn;
import org.jtester.exception.ExceptionWrapper;
import org.jtester.fit.util.SymbolUtil;
import org.jtester.module.database.JTesterDataSource;
import org.jtester.module.database.environment.typesmap.AbstractTypeMap;
import org.jtester.module.database.util.DataSourceType;
import org.jtester.module.dbfit.db.model.BigDecimalParseDelegate;
import org.jtester.module.dbfit.db.model.DbParameterAccessor;
import org.jtester.module.dbfit.db.model.SqlDateParseDelegate;
import org.jtester.module.dbfit.db.model.SqlTimestampParseDelegate;
import org.jtester.utility.JTesterLogger;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import fit.TypeAdapter;

public abstract class AbstractDBEnvironment implements DBEnvironment {

	protected Connection connection;

	protected final String dataSourceName;

	protected final String dataSourceFrom;

	protected DataSourceType dataSourceType;

	private JTesterDataSource jtesterDataSource;

	private DataSource dataSourceProxy = null;

	protected AbstractTypeMap typeMap;

	/**
	 * 是否使用spring来管理事务
	 */
	private boolean hasTransaction;

	protected AbstractDBEnvironment(DataSourceType dataSourceType, String dataSourceName, String dataSourceFrom) {
		TypeAdapter.registerParseDelegate(BigDecimal.class, BigDecimalParseDelegate.class);
		TypeAdapter.registerParseDelegate(java.sql.Date.class, SqlDateParseDelegate.class);
		TypeAdapter.registerParseDelegate(java.sql.Timestamp.class, SqlTimestampParseDelegate.class);

		this.dataSourceName = dataSourceName;
		this.dataSourceFrom = dataSourceFrom;
		this.dataSourceType = dataSourceType;
	}

	public void setDataSource(String driver, String url, String schemas, String username, String password) {
		this.jtesterDataSource = new JTesterDataSource(dataSourceType, driver, url, schemas, username, password);
	}

	public DataSource getDataSource(boolean withTransactionManager) {
		this.dataSourceProxy = this.jtesterDataSource;
		if (withTransactionManager) {
			this.dataSourceProxy = new TransactionAwareDataSourceProxy(jtesterDataSource);
		}
		return this.dataSourceProxy;
	}

	public void close() {
		try {
			if (connection != null && connection.isClosed() == false) {
				commit();
				DataSourceUtils.doReleaseConnection(connection, this.dataSourceProxy);
				connection = null;
			}
		} catch (SQLException e) {
			throw new RuntimeException(String.format("close datasource[%s] connection error.",
					this.jtesterDataSource.toString()), e);
		}
	}

	/**
	 * 连接当前数据源
	 * 
	 * @return
	 */
	public Connection connect() throws SQLException {
		this.hasTransaction = false;
		RunIn runIn = DbFitContext.getRunIn();
		if (runIn == RunIn.TestCase) {
			boolean springAvailable = ClazzHelper.isClassAvailable(ClazzConst.Spring_DataSourceUtils);
			hasTransaction = springAvailable && TestedObject.isTransactionsEnabled();
		}
		this.dataSourceProxy = getDataSource(hasTransaction);

		connection = DataSourceUtils.doGetConnection(this.dataSourceProxy);
		if (connection == null || connection.isClosed()) {
			System.out.println();
		}

		connection.setAutoCommit(false);
		return connection;
	}

	public final void teardown() throws SQLException {
		boolean isDefaultDbEnv = isDefaultDBEnvironment();

		if (!(isDefaultDbEnv && this.hasTransaction && connection != null)) {
			commit();

			DataSourceUtils.doReleaseConnection(connection, this.dataSourceProxy);
			connection = null;
		}
	}

	/**
	 * 是否是默认的数据源
	 * 
	 * @return
	 */
	private boolean isDefaultDBEnvironment() {
		boolean isDefault = DEFAULT_DATASOURCE_NAME.equals(this.dataSourceName)
				&& DEFAULT_DATASOURCE_FROM.equals(dataSourceFrom);
		return isDefault;
	}

	/**
	 * any processing required to turn a string into something jdbc driver can
	 * process, can be used to clean up CRLF, externalise parameters if required
	 * etc.
	 */
	protected String parseCommandText(String commandText, String[] vars) {
		return commandText;
	}

	public final PreparedStatement createStatementWithBoundFixtureSymbols(String commandText) throws SQLException {
		String text = SymbolUtil.replacedBySymbols(commandText);
		String paramNames[] = extractParamNames(text);

		String sql = parseCommandText(text, paramNames);

		PreparedStatement cs = connection.prepareStatement(sql);
		for (int i = 0; i < paramNames.length; i++) {
			Object value = org.jtester.fit.util.SymbolUtil.getSymbol(paramNames[i]);
			cs.setObject(i + 1, value);
		}
		return cs;
	}

	public void commit() throws SQLException {
		if (connection != null && connection.isClosed() == false) {
			connection.commit();
			connection.setAutoCommit(false);
		}
	}

	public void rollback() throws SQLException {
		if (connection != null && connection.isClosed() == false) {
			connection.rollback();
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public int getExceptionCode(SQLException dbException) {
		return dbException.getErrorCode();
	}

	/**
	 * MUST RETURN PARAMETER NAMES IN EXACT ORDER AS IN STATEMENT. IF SINGLE
	 * PARAMETER APPEARS MULTIPLE TIMES, MUST BE LISTED MULTIPLE TIMES IN THE
	 * ARRAY ALSO
	 */
	public String[] extractParamNames(String commandText) {
		ArrayList<String> hs = new ArrayList<String>();
		Matcher mc = getParameterPattern().matcher(commandText);
		while (mc.find()) {
			String var = mc.group(1);
			if (SymbolUtil.hasSymbol(var)) {
				hs.add(var);
			}

		}
		String[] array = new String[hs.size()];
		return hs.toArray(array);
	}

	protected abstract Pattern getParameterPattern();

	/**
	 * by default, this will support retrieving a single autogenerated key via
	 * JDBC. DB environments which support automated column retrieval after
	 * insert, like oracle, should override this and put in parameters for OUT
	 * accessors
	 */
	public String buildInsertCommand(String tableName, DbParameterAccessor[] accessors) {
		StringBuilder sb = new StringBuilder("insert into ");
		sb.append(tableName).append("(");
		String comma = "";

		StringBuilder values = new StringBuilder();

		for (DbParameterAccessor accessor : accessors) {
			if (accessor.getDirection() == DbParameterAccessor.INPUT) {
				sb.append(comma);
				values.append(comma);
				sb.append(this.getFieldQuato()).append(accessor.getName()).append(this.getFieldQuato());
				// values.append(":").append(accessor.getName());
				values.append("?");
				comma = ",";
			}
		}
		sb.append(") values (");
		sb.append(values);
		sb.append(")");
		return sb.toString();
	}

	public String buildDeleteCommand(String tableName, DbParameterAccessor[] accessors) {
		StringBuilder sb = new StringBuilder("delete from " + tableName + " where ");
		String comma = "";
		for (DbParameterAccessor accessor : accessors) {
			if (accessor.getDirection() == DbParameterAccessor.INPUT) {
				sb.append(comma);
				sb.append(accessor.getName());
				sb.append("=?");
				comma = ",";
			}
		}
		return sb.toString();
	}

	/**
	 * by default, this is set to false.
	 * 
	 * @see org.jtester.module.database.environment.DBEnvironment#supportsOuputOnInsert()
	 */
	public boolean supportsOuputOnInsert() {
		return false;
	}

	/** Check the validity of the supplied connection. */
	public static void checkConnectionValid(final Connection conn) throws SQLException {
		if (conn == null || conn.isClosed()) {
			throw new IllegalArgumentException("No open connection to a database is available. "
					+ "Make sure your database is running and that you have connected before performing any queries.");
		}
	}

	/**
	 * 连接数据源，如果先前没有建立过连接的话
	 * 
	 * @param environment
	 * @return 返回数据源连接
	 * @throws SQLException
	 */
	public Connection connectIfNeeded() {
		try {
			Connection conn = this.getConnection();
			if (conn == null || conn.isClosed()) {
				conn = this.connect();
			}
			return conn;
		} catch (Exception e) {
			throw ExceptionWrapper.getUndeclaredThrowableExceptionCaused(e);
		}
	}

	private Map<String, TableMeta> metas = new HashMap<String, TableMeta>();

	/**
	 * 获得数据表的元信息
	 * 
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public TableMeta getTableMetaData(String table) {
		TableMeta meta = metas.get(table);
		if (meta == null) {
			try {
				this.connectIfNeeded();
				String query = "select * from " + table + " where 1!=1";
				PreparedStatement st = this.createStatementWithBoundFixtureSymbols(query);
				ResultSet rs = st.executeQuery();

				meta = new TableMeta(table, rs.getMetaData(), this);
				metas.put(table, meta);
			} catch (Exception e) {
				throw ExceptionWrapper.getUndeclaredThrowableExceptionCaused(e);
			}
		}
		return meta;
	}

	public Object getDefaultValue(String javaType) {
		Object value = this.typeMap.getDefaultValue(javaType);
		return value;
	}

	public Object toObjectValue(String input, String javaType) {
		try {
			Object value = this.typeMap.toObjectByType(input, javaType);
			return value;
		} catch (Exception e) {
			JTesterLogger.info("convert input[" + input + "] to type[" + javaType + "] error, so return input value.\n"
					+ e.getMessage());
			return input;
		}
	}
	
	@Override
	public boolean isfilterMetaDataNeeded(String typeName) {
		return false;
	}
	
	@Override
	public void resetPrimaryKey(String table) {
		
	}
	
	@Override
	public String preExecute(String table, String sql) {
		return sql;
	}
}