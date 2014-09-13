package org.jtester.module.dbfit;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.jtester.core.TestedObject;
import org.jtester.core.context.DbFitContext;
import org.jtester.core.context.DbFitContext.RunIn;
import org.jtester.core.dbfit.DbFitOp;
import org.jtester.fit.util.SymbolUtil;
import org.jtester.module.database.environment.DBEnvironment;
import org.jtester.module.database.environment.DBEnvironmentFactory;
import org.jtester.module.database.util.SqlRunner;
import org.jtester.module.dbfit.db.fixture.CleanFixture;
import org.jtester.module.dbfit.db.fixture.CompareStoredQueriesFixture;
import org.jtester.module.dbfit.db.fixture.DeleteFixture;
import org.jtester.module.dbfit.db.fixture.ExecuteProcedureFixture;
import org.jtester.module.dbfit.db.fixture.InsertFixture;
import org.jtester.module.dbfit.db.fixture.InspectFixture;
import org.jtester.module.dbfit.db.fixture.QueryFixture;
import org.jtester.module.dbfit.db.fixture.QueryStatsFixture;
import org.jtester.module.dbfit.db.fixture.StoreQueryFixture;
import org.jtester.module.dbfit.db.fixture.StoreQueryTableFixture;
import org.jtester.module.dbfit.db.fixture.UpdateFixture;
import org.jtester.utility.DateUtil;
import org.jtester.utility.JTesterLogger;

import fit.Fixture;
import fitlibrary.SequenceFixture;
import fitlibrary.table.Table;
import fitlibrary.utility.TestResults;

public class DatabaseFixture extends SequenceFixture implements DbFitOp {

	public DatabaseFixture() {
	}

	@Override
	public void setUp(Table firstTable, TestResults testResults) {
		super.setUp(firstTable, testResults);
	}

	@Override
	public void tearDown(Table firstTable, TestResults testResults) {
		DBEnvironment environment = workingEnvironment();

		try {
			JTesterLogger.info("tearDown dbfit table");
			if (environment == null) {
				return;
			}
			RunIn runIn = DbFitContext.getRunIn();
			boolean isEnabledTransaction = TestedObject.isTransactionsEnabled();
			if (runIn == RunIn.TestCase) {
				JTesterLogger.info("run in testcase, isEnabledTransaction:" + isEnabledTransaction);
			}

			if (runIn == RunIn.TestCase && isEnabledTransaction == false) {
				commit();
			}
			environment.teardown();
		} catch (Throwable e) {
			this.exception(firstTable.parse, e);
		}
		super.tearDown(firstTable, testResults);
	}

	public boolean connect() throws SQLException {
		DBEnvironment environment = workingEnvironment();
		environment.connect();
		return true;
	}

	public boolean connect(String type, String driver, String url, String username, String password) throws Exception {
		DBEnvironment environment = DBEnvironmentFactory.getDBEnvironment(type, driver, url, username, password);
		DBEnvironmentFactory.changeDBEnvironment(environment);
		environment.connect();
		return true;
	}

	final static String NO_VALID_VALUE_MESSAGE = "can't find valid value of key[%s] in file[%s]!";

	public boolean connectFromFile(String dbname) throws Exception {
		DBEnvironment environment = DBEnvironmentFactory.getDBEnvironment(dbname);
		DBEnvironmentFactory.changeDBEnvironment(environment);
		environment.connect();

		return true;
	}

	public boolean connectFromFile(String dataSourceName, String propFile) throws Exception {
		DBEnvironment environment = DBEnvironmentFactory.getDBEnvironment(dataSourceName, propFile);
		DBEnvironmentFactory.changeDBEnvironment(environment);
		environment.connect();

		return true;
	}

	// public boolean close() throws SQLException {
	// DBEnvironment enviroment = workingEnvironment();
	// enviroment.close();
	// return true;
	// }

	public boolean setParameter(String name, String value) {
		DbFixtureUtil.setParameter(name, value);
		return true;
	}

	public Fixture storeQuery(String query, String symbolName) {
		DBEnvironment environment = workingEnvironment();
		return new StoreQueryFixture(environment, query, symbolName);
	}

	/**
	 * 设置当前时间格式
	 * 
	 * @param format
	 * @return
	 */
	public boolean setDateTimeFormat(String format) {
		String datetime = DateUtil.currDateTimeStr(format);
		DbFixtureUtil.setParameter("datetime", datetime);
		return true;
	}

	/**
	 * 设置当前日期格式
	 * 
	 * @param format
	 * @return
	 */
	public boolean setDateFormat(String format) {
		String date = DateUtil.currDateTimeStr(format);
		DbFixtureUtil.setParameter("date", date);
		return true;
	}

	public boolean clearParameters() {
		SymbolUtil.cleanSymbols();
		return true;
	}

	public Fixture query(String query) {
		DBEnvironment environment = workingEnvironment();
		return new QueryFixture(environment, query);
	}

	public Fixture orderedQuery(String query) {
		DBEnvironment environment = workingEnvironment();
		return new QueryFixture(environment, query, true);
	}

	public boolean execute(String statement) {
		DBEnvironment environment = workingEnvironment();
		return DbFixtureUtil.execute(environment, statement);
	}

	public Fixture executeProcedure(String statement) {
		DBEnvironment environment = workingEnvironment();
		return new ExecuteProcedureFixture(environment, statement);
	}

	public Fixture executeProcedureExpectException(String statement) {
		DBEnvironment environment = workingEnvironment();
		return new ExecuteProcedureFixture(environment, statement, true);
	}

	public Fixture executeProcedureExpectException(String statement, int code) {
		DBEnvironment environment = workingEnvironment();
		return new ExecuteProcedureFixture(environment, statement, code);
	}

	public Fixture insert(String tableName) {
		DBEnvironment environment = workingEnvironment();
		return new InsertFixture(environment, tableName);
	}

	public Fixture update(String tableName) {
		DBEnvironment environment = workingEnvironment();
		return new UpdateFixture(environment, tableName);
	}

	public Fixture clean() {
		DBEnvironment environment = workingEnvironment();
		return new CleanFixture(environment);
	}

	public boolean cleanTable(String tables) {
		DBEnvironment environment = workingEnvironment();
		String ts[] = tables.split("[;,]");
		for (String table : ts) {
			DbFixtureUtil.cleanTable(environment, table);
		}
		return true;
	}

	/**
	 * 根据表字段删除数据
	 * 
	 * @param table
	 * @return
	 */
	public Fixture delete(String table) {
		DBEnvironment environment = workingEnvironment();
		return new DeleteFixture(environment, table);
	}

	public boolean rollback() throws SQLException {
		DBEnvironment environment = workingEnvironment();
		environment.rollback();
		environment.getConnection().setAutoCommit(false);
		return true;
	}

	public boolean commit() throws SQLException {
		DBEnvironment environment = workingEnvironment();
		environment.commit();
		environment.getConnection().setAutoCommit(false);
		return true;
	}

	public Fixture queryStats() {
		DBEnvironment environment = workingEnvironment();
		return new QueryStatsFixture(environment);
	}

	public Fixture inspectProcedure(String procName) {
		DBEnvironment environment = workingEnvironment();
		return new InspectFixture(environment, InspectFixture.MODE_PROCEDURE, procName);
	}

	public Fixture inspectTable(String tableName) {
		DBEnvironment environment = workingEnvironment();
		return new InspectFixture(environment, InspectFixture.MODE_TABLE, tableName);
	}

	public Fixture inspectView(String tableName) {
		DBEnvironment environment = workingEnvironment();
		return new InspectFixture(environment, InspectFixture.MODE_TABLE, tableName);
	}

	public Fixture inspectQuery(String query) {
		DBEnvironment environment = workingEnvironment();
		return new InspectFixture(environment, InspectFixture.MODE_QUERY, query);
	}

	/**
	 * 把查询结果的整张表作为结果存储下来 <br>
	 * storeQuery把查询结果是单个值，以变量形式存储下来
	 * 
	 * @param query
	 * @param symbolName
	 * @return
	 */
	public Fixture storeQueryTable(String query, String symbolName) {
		DBEnvironment environment = workingEnvironment();
		return new StoreQueryTableFixture(environment, query, symbolName);
	}

	public Fixture compareStoredQueries(String symbol1, String symbol2) {
		DBEnvironment environment = workingEnvironment();
		return new CompareStoredQueriesFixture(environment, symbol1, symbol2);
	}

	/**
	 * 执行指定文件中的sql语句
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws SQLException
	 */
	public boolean executeFile(String file) throws Exception {
		SqlRunner.executeFromFile(file);
		return true;
	}

	/**
	 * 返回当前的数据库环境
	 * 
	 * @return
	 */
	public DBEnvironment workingEnvironment() {
		DBEnvironment environment = DBEnvironmentFactory.getCurrentDBEnvironment();
		return environment;
	}
}
