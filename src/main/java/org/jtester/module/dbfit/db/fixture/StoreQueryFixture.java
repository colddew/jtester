package org.jtester.module.dbfit.db.fixture;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.jtester.fit.JTesterFixture;
import org.jtester.module.database.environment.DBEnvironment;
import org.jtester.module.database.util.DBHelper;
import org.jtester.module.dbfit.db.model.DataRow;
import org.jtester.module.dbfit.db.model.DataTable;

import fit.Parse;

public class StoreQueryFixture extends JTesterFixture {
	private DBEnvironment dbEnvironment;
	private String query;
	private String symbolName;

	// public StoreQueryFixture() {
	// dbEnvironment = DbFactory.instance().factory();//
	// DbEnvironmentFactory.getDefaultEnvironment();
	// }

	public StoreQueryFixture(DBEnvironment environment, String query, String symbolName) {
		this.dbEnvironment = environment;
		this.query = query;
		this.symbolName = symbolName;
	}

	public void doTable(Parse table) {
		if (query == null || symbolName == null) {
			if (args.length < 2)
				throw new UnsupportedOperationException(
						"No query and symbol name specified to StoreQuery constructor or argument list");
			query = args[0];
			symbolName = args[1];
		}
		if (symbolName.startsWith(">>")) {
			symbolName = symbolName.substring(2);
		}
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = dbEnvironment.createStatementWithBoundFixtureSymbols(query);
			rs = st.executeQuery();
			DataTable dt = new DataTable(rs);

			int rowSize = dt.getRowSize();
			if (rowSize == 0) {
				throw new RuntimeException("can't retrieve any data by query:" + query);
			} else if (rowSize > 1) {
				throw new RuntimeException("allowing one row data retrieved by query:" + query);
			}
			int colSize = dt.getColSize();
			if (colSize == 0) {
				throw new RuntimeException("can't retrieve any column by query:" + query);
			} else if (colSize > 1) {
				throw new RuntimeException("allowing one column data retrieved by query:" + query);
			}
			DataRow row = dt.getUnprocessedRows().get(0);
			List<String> values = row.getStringValues();
			org.jtester.fit.util.SymbolUtil.setSymbol(symbolName, values.get(0));
		} catch (Exception sqle) {
			throw new Error(sqle);
		} finally {
			DBHelper.closeResultSet(rs);
			rs = null;
			DBHelper.closeStatement(st);
			st = null;
		}
	}
}