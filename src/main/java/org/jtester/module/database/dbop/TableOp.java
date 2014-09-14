package org.jtester.module.database.dbop;

import static org.jtester.module.database.dbop.DBOperator.IN_DB_OPERATOR;

import java.util.List;

import org.jtester.core.IJTester.DataMap;
import org.jtester.hamcrest.iassert.object.impl.CollectionAssert;
import org.jtester.hamcrest.iassert.object.impl.LongAssert;
import org.jtester.hamcrest.iassert.object.impl.ObjectAssert;
import org.jtester.hamcrest.iassert.object.intf.ICollectionAssert;
import org.jtester.hamcrest.iassert.object.intf.INumberAssert;
import org.jtester.hamcrest.iassert.object.intf.IObjectAssert;
import org.jtester.json.JSON;
import org.jtester.module.database.dbop.AbstractDataSet.EmptyDataSet;
import org.jtester.module.database.util.DBHelper;
import org.jtester.module.database.util.SqlRunner;
import org.jtester.utility.StringHelper;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TableOp implements ITableOp {
	
	public static final long serialVersionUID = -5859213164627788602L;

	private String table;

	public TableOp(String table) {
		this.table = table;
		if (StringHelper.isBlankOrNull(this.table)) {
			throw new RuntimeException("the table name can't be null.");
		}
	}

	public ITableOp clean() {
		IN_DB_OPERATOR.set(true);
		try {
			String sql = "delete from " + table;
			SqlRunner.execute(sql);
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}
	
	public ITableOp resetPrimaryKey() {
		IN_DB_OPERATOR.set(true);
		try {
			SqlRunner.resetPrimaryKey(table);
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public void commit() {
		IN_DB_OPERATOR.set(true);
		try {
			SqlRunner.commit();
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public void rollback() {
		IN_DB_OPERATOR.set(true);
		try {
			SqlRunner.rollback();
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public ITableOp insert(DataMap data, DataMap... more) {
		IN_DB_OPERATOR.set(true);
		try {
			InsertOp.insert(table, data);
			for (DataMap map : more) {
				InsertOp.insert(table, map);
			}
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public ITableOp insert(String json, String... more) {
		IN_DB_OPERATOR.set(true);
		try {
			DataMap map = JSON.toObject(json, DataMap.class);
			InsertOp.insert(table, map);
			for (String item : more) {
				map = JSON.toObject(item, DataMap.class);
				InsertOp.insert(table, map);
			}
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public ITableOp insert(final int count, final DataMap datas) {
		IN_DB_OPERATOR.set(true);
		try {
			AbstractDataSet ds = new EmptyDataSet();
			ds.data(count, datas);
			ds.insert(table);
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public ITableOp insert(AbstractDataSet dataset) {
		IN_DB_OPERATOR.set(true);
		try {
			if (dataset == null) {
				throw new RuntimeException("the insert dataset can't be null.");
			}
			dataset.insert(table);
			return this;
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public ICollectionAssert query() {
		IN_DB_OPERATOR.set(true);
		try {
			String query = "select * from " + table;
			List list = SqlRunner.queryMapList(query);
			return new CollectionAssert(list);
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public ICollectionAssert queryList(Class pojo) {
		IN_DB_OPERATOR.set(true);
		try {
			String query = "select * from " + table;
			List list = SqlRunner.queryList(query, pojo);
			return new CollectionAssert(list);
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public INumberAssert count() {
		IN_DB_OPERATOR.set(true);
		try {
			String query = "select count(*) from " + table;
			Number number = (Number) SqlRunner.query(query, Object.class);
			return new LongAssert(number.longValue());
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public IObjectAssert queryAs(Class pojo) {
		IN_DB_OPERATOR.set(true);
		try {
			String query = "select * from " + table;
			Object o = SqlRunner.query(query, pojo);
			return new ObjectAssert(o);
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public ICollectionAssert queryWhere(String where) {
		IN_DB_OPERATOR.set(true);
		try {
			String query = "select * from " + table + " where " + where;
			List list = SqlRunner.queryMapList(query);
			return new CollectionAssert(list);
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}

	public ICollectionAssert queryWhere(DataMap dataMap) {
		IN_DB_OPERATOR.set(true);
		try {
			StringBuilder query = new StringBuilder("select * from ");
			query.append(table).append(" ");
			String where = DBHelper.getWhereCondiction(dataMap);
			query.append(where);
			List list = SqlRunner.queryMapList(query.toString(), dataMap);
			return new CollectionAssert(list);
		} finally {
			IN_DB_OPERATOR.set(false);
		}
	}
}
