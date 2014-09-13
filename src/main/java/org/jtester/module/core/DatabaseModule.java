package org.jtester.module.core;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.jtester.annotations.Transactional;
import org.jtester.annotations.Transactional.TransactionMode;
import org.jtester.core.TestedObject;
import org.jtester.core.context.TransactionManager;
import org.jtester.module.TestListener;
import org.jtester.module.core.helper.ConfigurationHelper;
import org.jtester.module.database.environment.DBEnvironmentFactory;
import org.jtester.module.database.support.DefaultSQLHandler;
import org.jtester.module.database.support.SQLHandler;
import org.jtester.module.database.util.ConstraintsDisabler;
import org.jtester.utility.JTesterLogger;
import org.springframework.transaction.PlatformTransactionManager;

import com.ibatis.sqlmap.engine.datasource.DataSourceFactory;

/**
 * Module that provides support for database testing: Creation of a datasource
 * that connects to the test database, support for executing tests in a
 * transaction and automatic maintenance of the test database.
 * <p/>
 * A datasource will be created the first time one is requested. Which type of
 * datasource will be created depends on the configured
 * {@link DataSourceFactory}. By default this will be a pooled datasource that
 * gets its connection-url, username and password from the unitils
 * configuration.
 * <p/>
 * The created datasource can be injected into a field of the test by annotating
 * the field with {@link TestDataSource}. It can then be used to install it in
 * your DAO or other class under test.
 * <p/>
 * If the DBMaintainer is enabled (by setting
 * {@link #PROPERTY_UPDATEDATABASESCHEMA_ENABLED} to true), the test database
 * schema will automatically be updated if needed. This check will be performed
 * once during your test-suite run, namely when the data source is created.
 * <p/>
 * If the test class or method is annotated with {@link Transactional} with
 * transaction mode {@link TransactionMode#COMMIT} or
 * {@link TransactionMode#ROLLBACK}, or if the property
 * 'DatabaseModule.Transactional.value.default' was set to 'commit' or
 * 'rollback', every test is executed in a transaction.
 * 
 */
public class DatabaseModule implements Module {

	/**
	 * Property indicating if the database schema should be updated before
	 * performing the tests
	 */
	public static final String PROPERTY_UPDATEDATABASESCHEMA_ENABLED = "updateDataBaseSchema.enabled";

	/**
	 * Initializes this module using the given <code>Configuration</code>
	 * 
	 * @param configuration
	 *            The config, not null
	 */
	public void init() {
		JTesterLogger.info("PlatformTransactionManager class init.");
		PlatformTransactionManager.class.getName();
	}

	/**
	 * Initializes the spring support object<br>
	 * <br>
	 * 判断是否需要去除数据库的外键约束和字段not null约束<br>
	 * <br>
	 * Disables all foreign key and not-null constraints on the configured
	 * schema's.
	 */
	public void afterInit() {
		boolean disabledConstraint = ConfigurationHelper.doesDisableConstraints();
		if (disabledConstraint) {
			disableConstraints();
		}
	}

	/**
	 * Disables all foreigh key and not-null constraints on the configured
	 * schema's.
	 */
	public void disableConstraints() {
		DataSource dataSource = DBEnvironmentFactory.getDBEnvironment().getDataSource(false);
		SQLHandler sqlHandler = new DefaultSQLHandler(dataSource);

		String databaseDialect = ConfigurationHelper.getString(ConfigurationHelper.PROPKEY_DATABASE_DIALECT);
		ConstraintsDisabler disabler = ConfigurationHelper.getInstanceOf(ConstraintsDisabler.class, databaseDialect);
		disabler.init(sqlHandler);
		disabler.disableConstraints();
	}

	/**
	 * @return The {@link TestListener} associated with this module
	 */
	public TestListener getTestListener() {
		return new DatabaseTestListener();
	}

	protected static boolean hasExetedDisabled = false;

	/**
	 * The {@link TestListener} for this module
	 */
	protected class DatabaseTestListener extends TestListener {

		/**
		 * 初始化测试方法的事务<br>
		 * <br> {@inheritDoc}
		 */
		@Override
		public void setupMethod(Object testObject, Method testMethod) {
			TestedObject.setLocalTransactionManager();
		}

		@Override
		public void beforeMethodRunning(Object testObject, Method testMethod) {
			boolean isEnabledTransaction = TestedObject.isTransactionsEnabled();
			TransactionManager transaction = TestedObject.getLocalTransactionManager();
			if (isEnabledTransaction && transaction != null) {
				transaction.startTransaction();
			}
		}

		@Override
		public void afterMethodRunned(Object testObject, Method testMethod, Throwable testThrowable) {
			boolean isEnabledTransaction = TestedObject.isTransactionsEnabled();
			TransactionManager transaction = TestedObject.getLocalTransactionManager();
			if (isEnabledTransaction && transaction != null) {
				transaction.endTransaction();
			}
		}

		/**
		 * 移除测试方法的事务<br>
		 * <br>{@inheritDoc}
		 */
		@Override
		public void teardownMethod(Object testObject, Method testMethod) {
			TestedObject.removeLocalTransactionManager();
		}

		@Override
		protected String getName() {
			return "DatabaseTestListener";
		}
	}
}
