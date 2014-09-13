package org.jtester.module.database.dbop;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.jtester.exception.ExceptionWrapper;
import org.jtester.hamcrest.iassert.object.impl.CollectionAssert;
import org.jtester.hamcrest.iassert.object.impl.MapAssert;
import org.jtester.hamcrest.iassert.object.impl.ObjectAssert;
import org.jtester.hamcrest.iassert.object.intf.ICollectionAssert;
import org.jtester.hamcrest.iassert.object.intf.IMapAssert;
import org.jtester.hamcrest.iassert.object.intf.IObjectAssert;
import org.jtester.module.database.environment.DBEnvironment;
import org.jtester.module.database.environment.DBEnvironmentFactory;
import org.jtester.module.database.util.SqlRunner;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DBOperator implements IDBOperator {
	/**
	 * 是否在db操作
	 */
	public static ThreadLocal<Boolean> IN_DB_OPERATOR = new ThreadLocal<Boolean>();
	static {
		IN_DB_OPERATOR.set(false);
	}

	public IDBOperator useDB(String dataSource) {
		IN_DB_OPERATOR.set(true);
		try {
			DBEnvironment environment = DBEnvironmentFactory.getDBEnvironment(dataSource);
			DBEnvironmentFactory.changeDBEnvironment(environment);
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public IDBOperator useDefaultDB() {
		IN_DB_OPERATOR.set(true);
		try {
			DBEnvironment environment = DBEnvironmentFactory.getDBEnvironment();
			DBEnvironmentFactory.changeDBEnvironment(environment);
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public IDBOperator cleanTable(String table, String... more) {
		IN_DB_OPERATOR.set(true);
		try {
			SqlRunner.execute("delete from " + table);
			for (String item : more) {
				SqlRunner.execute("delete from " + item);
			}
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public IDBOperator execute(String sql) {
		IN_DB_OPERATOR.set(true);
		try {
			SqlRunner.execute(sql);
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public IDBOperator execute(File file) {
		IN_DB_OPERATOR.set(true);
		try {
			try {
				SqlRunner.executeFromStream(new FileInputStream(file));
			} catch (Exception e) {
				throw ExceptionWrapper.wrapWithRuntimeException(e);
			}
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public IDBOperator commit() {
		IN_DB_OPERATOR.set(true);
		try {
			SqlRunner.commit();
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public IDBOperator rollback() {
		IN_DB_OPERATOR.set(true);
		try {
			SqlRunner.rollback();
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public IMapAssert queryAsMap(String query) {
		IN_DB_OPERATOR.set(true);
		try {
			Map<String, Object> map = SqlRunner.queryMap(query);
			return new MapAssert(map);
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public IObjectAssert queryAsPoJo(String query, Class objClazz) {
		IN_DB_OPERATOR.set(true);
		try {
			Object o = SqlRunner.query(query, objClazz);
			return new ObjectAssert(o);
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public ICollectionAssert query(String sql) {
		IN_DB_OPERATOR.set(true);
		try {
			List list = SqlRunner.queryMapList(sql);
			return new CollectionAssert(list);
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public ICollectionAssert queryList(String query, Class pojo) {
		IN_DB_OPERATOR.set(true);
		try {
			List list = SqlRunner.queryList(query, pojo);
			return new CollectionAssert(list);
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public ITableOp table(String table) {
		IN_DB_OPERATOR.set(true);
		try {
			ITableOp tableOperator = new TableOp(table);
			return tableOperator;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public IDBOperator execute(SqlSet sqlSet) {
		IN_DB_OPERATOR.set(true);
		try {
			if (sqlSet == null) {
				throw new RuntimeException("the insert sqlSet can't be null.");
			}
			sqlSet.execute();
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public List<Map<String, Object>> returnList(String table) {
		IN_DB_OPERATOR.set(true);
		try {
			String query = "select * from " + table;
			List list = SqlRunner.queryMapList(query);
			return list;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public List<Object> returnList(String table, Class pojoClazz) {
		IN_DB_OPERATOR.set(true);
		try {
			String query = "select * from " + table;
			List list = SqlRunner.queryList(query, pojoClazz);
			return list;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public List<Map<String, Object>> returnQuery(String query) {
		IN_DB_OPERATOR.set(true);
		try {
			List list = SqlRunner.queryMapList(query);
			return list;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public List<Object> returnQuery(String query, Class pojoClazz) {
		IN_DB_OPERATOR.set(true);
		try {
			List list = SqlRunner.queryList(query, pojoClazz);
			return list;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}
}
