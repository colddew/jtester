package org.jtester.module.dbfit.db.fixture;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.jtester.fit.JTesterFixture;
import org.jtester.module.database.environment.DBEnvironment;
import org.jtester.module.database.util.DBHelper;
import org.jtester.module.dbfit.db.model.DbParameterAccessor;

import fit.Fixture;
import fit.Parse;

public class InspectFixture extends JTesterFixture {
	private DBEnvironment environment;
	private String objectName;
	private String mode;
	public static String MODE_PROCEDURE = "PROCEDURE";
	public static String MODE_TABLE = "TABLE";
	public static String MODE_QUERY = "QUERY";

	// public InspectFixture() {
	// this.environment = DbFactory.instance().factory();//
	// DbEnvironmentFactory.getDefaultEnvironment();
	// }

	public InspectFixture(DBEnvironment dbEnvironment, String mode, String objName) {
		this.objectName = objName;
		this.mode = mode;
		this.environment = dbEnvironment;
	}

	public void doTable(Parse table) {
		if (objectName == null)
			objectName = args[0];
		try {
			if (MODE_PROCEDURE.equalsIgnoreCase(mode))
				inspectProcedure(table);
			else if (MODE_TABLE.equalsIgnoreCase(mode))
				inspectTable(table);
			else if (MODE_QUERY.equalsIgnoreCase(mode))
				inspectQuery(table);
			else
				throw new Exception("Unknown inspect mode " + mode);
		} catch (Throwable e) {
			exception(table.parts.parts, e);
		}
	}

	private void inspectTable(Parse table) throws SQLException {
		Map<String, DbParameterAccessor> allParams = environment.getAllColumns(objectName);
		if (allParams.isEmpty()) {
			throw new SQLException("Cannot retrieve list of columns for table or view " + objectName
					+ " - check spelling and access rights");
		}
		addRowWithParamNames(table, allParams);
	}

	private void inspectProcedure(Parse table) throws SQLException {
		Map<String, DbParameterAccessor> allParams = environment.getAllProcedureParameters(objectName);
		if (allParams.isEmpty()) {
			throw new SQLException("Cannot retrieve list of parameters for procedure " + objectName
					+ " - check spelling and access rights");
		}
		addRowWithParamNames(table, allParams);
	}

	private void inspectQuery(Parse table) throws Exception {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = environment.createStatementWithBoundFixtureSymbols(objectName);
			rs = st.executeQuery();
			Parse newRow = getHeaderFromRS(rs);
			table.parts.more = newRow;
			while (rs.next()) {
				newRow.more = getDataRow(rs);
				newRow = newRow.more;
			}
		} finally {
			DBHelper.closeResultSet(rs);
			rs = null;
			DBHelper.closeStatement(st);
			st = null;
		}
	}

	private Parse getDataRow(ResultSet rs) throws Exception {
		Parse newRow = new Parse("tr", null, null, null);
		ResultSetMetaData rsmd = rs.getMetaData();
		Parse prevCell = null;
		for (int i = 0; i < rsmd.getColumnCount(); i++) {
			Object value = rs.getObject(i + 1);
			value = DbParameterAccessor.normaliseValue(value);
			Parse cell = new Parse("td", Fixture.gray(value == null ? "null" : value.toString()), null, null);
			if (prevCell == null) {
				newRow.parts = cell;
			} else {
				prevCell.more = cell;
			}
			prevCell = cell;
		}
		return newRow;

	}

	private Parse getHeaderFromRS(ResultSet rs) throws SQLException {
		Parse newRow = new Parse("tr", null, null, null);
		ResultSetMetaData rsmd = rs.getMetaData();
		Parse prevCell = null;
		for (int i = 0; i < rsmd.getColumnCount(); i++) {
			Parse cell = new Parse("td", Fixture.gray(rsmd.getColumnName(i + 1)), null, null);
			if (prevCell == null)
				newRow.parts = cell;
			else
				prevCell.more = cell;
			prevCell = cell;
		}
		return newRow;
	}

	private void addRowWithParamNames(Parse table, Map<String, DbParameterAccessor> params) {
		Parse newRow = new Parse("tr", null, null, null);
		table.parts.more = newRow;
		Parse prevCell = null;
		String orderedNames[] = new String[params.size()];
		for (String s : params.keySet()) {
			orderedNames[params.get(s).getPosition()] = s;
		}
		for (int i = 0; i < orderedNames.length; i++) {
			String name = orderedNames[i];
			if (params.get(name).getDirection() != DbParameterAccessor.INPUT)
				name = name + "?";
			Parse cell = new Parse("td", Fixture.gray(name), null, null);
			if (prevCell == null)
				newRow.parts = cell;
			else
				prevCell.more = cell;
			prevCell = cell;
		}
	}
}