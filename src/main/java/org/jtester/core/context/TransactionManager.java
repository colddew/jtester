package org.jtester.core.context;

import static org.jtester.annotations.Transactional.TransactionMode.COMMIT;
import static org.jtester.annotations.Transactional.TransactionMode.DEFAULT;
import static org.jtester.annotations.Transactional.TransactionMode.DISABLED;

import javax.sql.DataSource;

import org.jtester.annotations.Transactional.TransactionMode;
import org.jtester.core.TestedObject;
import org.jtester.exception.UnConfigDataBaseTypeException;
import org.jtester.module.database.environment.DBEnvironment;
import org.jtester.module.database.environment.DBEnvironmentFactory;
import org.jtester.utility.JTesterLogger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 测试方法的的事务管理<br>
 * 事务是方法级别的（可能有多线程测试）
 * 
 * @author darui.wudr
 * 
 */
public class TransactionManager {

	private TransactionStatus transactionStatus;

	private PlatformTransactionManager transactionManager;

	public TransactionManager() {
		this.transactionManager = null;
		this.transactionStatus = null;
	}

	/**
	 * 开始测试事务<br>
	 * <br>
	 * Starts a new transaction on the transaction manager configured in jTester
	 */
	public void startTransaction() {
		try {
			DBEnvironment environment = DBEnvironmentFactory.getCurrentDBEnvironment();
			DataSource dataSource = environment.getDataSource(true);
			PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager(dataSource);

			setTransaction(platformTransactionManager);
		} catch (UnConfigDataBaseTypeException e) {
			setTransaction(null);
		}
	}

	/**
	 * 结束测试事务<br>
	 * <br>
	 * End an already existed transaction and remove it.
	 */
	public void endTransaction() {
		TransactionMode mode = TestedObject.getTransactionMode();
		if (mode == null || mode == DISABLED || mode == DEFAULT) {
			return;
		}
		if (mode == COMMIT) {
			commit();
		} else {
			rollback();
		}
	}
	
	public void forceEnd() {
		commit();
	}

	/**
	 * 设置事务管理，如果transactionManager=null,则没有事务管理
	 * 
	 * @param transactionManager
	 */
	private void setTransaction(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
		if (this.transactionManager != null) {
			TransactionDefinition transactionDefinition = new DefaultTransactionDefinition(
					TransactionDefinition.PROPAGATION_REQUIRED);
			this.transactionStatus = transactionManager.getTransaction(transactionDefinition);
		}
	}

	/**
	 * Commits the transaction. Uses the PlatformTransactionManager and
	 * transaction that is associated with the given test object.
	 */
	private void commit() {
		if (this.transactionManager == null) {
			return;
		}
		try {
			JTesterLogger.debug("commit transaction");
			this.transactionManager.commit(this.transactionStatus);
		} catch (UnexpectedRollbackException e) {
			StringBuffer message = new StringBuffer();
			message.append("Catch a transaction exception: org.springframework.transaction.UnexpectedRollbackException.\n");
			message.append("\tplease use @Transactional(TransactionMode.DISABLED) on test method.\n");
			message.append("\tException:" + e.getMessage());
			JTesterLogger.warn(message.toString());
		} finally {
			this.transactionManager = null;
			this.transactionStatus = null;
		}
	}

	/**
	 * Rolls back the transaction. Uses the PlatformTransactionManager and
	 * transaction that is associated with the given test object.
	 */
	private void rollback() {
		if (this.transactionManager == null) {
			return;
		}
		try {
			JTesterLogger.debug("Rolling back transaction");
			this.transactionManager.rollback(this.transactionStatus);
		} finally {
			this.transactionManager = null;
			this.transactionStatus = null;
		}
	}

	/**
	 * 测试是否已经存在事务，如果已经有事务，则抛出异常<br>
	 * 用来debug用函数
	 */
	public final static void testIfHasTransactional() {
		PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager(DBEnvironmentFactory
				.getCurrentDBEnvironment().getDataSource(false));
		TransactionStatus status = platformTransactionManager.getTransaction(new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_NEVER));
		platformTransactionManager.commit(status);
	}

	/**
	 * 强行终止spring事务,如果已经存在
	 */
	public final static void forceEndTransactional() {
		PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager(DBEnvironmentFactory
				.getCurrentDBEnvironment().getDataSource(false));
		TransactionStatus status = platformTransactionManager.getTransaction(new DefaultTransactionDefinition(
				TransactionDefinition.PROPAGATION_REQUIRED));
		platformTransactionManager.rollback(status);
	}
}
