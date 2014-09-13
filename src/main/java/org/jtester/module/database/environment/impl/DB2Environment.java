package org.jtester.module.database.environment.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jtester.module.database.environment.AbstractDBEnvironment;
import org.jtester.module.database.environment.normalise.NameNormaliser;
import org.jtester.module.database.util.DBHelper;
import org.jtester.module.database.util.DataSourceType;
import org.jtester.module.dbfit.db.model.DbParameterAccessor;

@SuppressWarnings("rawtypes")
public class DB2Environment extends AbstractDBEnvironment {

	public DB2Environment(String dataSourceName, String dataSourceFrom) {
		super(DataSourceType.DB2, dataSourceName, dataSourceFrom);
		// TODO
		this.typeMap = null;
	}

	protected String parseCommandText(String commandText, String[] vars) {
		if (vars == null || vars.length == 0) {
			return commandText;
		}
		String sql = commandText;
		for (String var : vars) {
			sql = sql.replace("@" + var, "?").replace(":" + var, "?");
		}
		return sql;
	}

	private static String paramNamePattern = "[@:]([A-Za-z0-9_]+)";
	private static Pattern paramRegex = Pattern.compile(paramNamePattern);

	public Pattern getParameterPattern() {
		return paramRegex;
	}

	public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName) throws SQLException {
		String[] qualifiers = NameNormaliser.normaliseName(tableOrViewName).split("\\.");
		String qry = " select colname as column_name, typename as data_type, length, "
				+ "	'P' as direction from syscat.columns where ";
		if (qualifiers.length == 2) {
			qry += " lower(tabschema)=? and lower(tabname)=? ";
		} else {
			qry += " (lower(tabname)=?)";
		}
		qry += " order by colname";
		return readIntoParams(qualifiers, qry);
	}

	private Map<String, DbParameterAccessor> readIntoParams(String[] queryParameters, String query) throws SQLException {
		PreparedStatement dc = null;
		ResultSet rs = null;
		try {
			dc = connection.prepareStatement(query);

			for (int i = 0; i < queryParameters.length; i++) {
				dc.setString(i + 1, NameNormaliser.normaliseName(queryParameters[i]));
			}
			rs = dc.executeQuery();
			Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();
			int position = 0;
			while (rs.next()) {
				String paramName = rs.getString(1);
				if (paramName == null)
					paramName = "";
				String dataType = rs.getString(2);
				// int length=rs.getInt(3);
				String direction = rs.getString(4);
				int paramDirection = getParameterDirection(direction);
				DbParameterAccessor dbp = new DbParameterAccessor(paramName, paramDirection, getSqlType(dataType),
						getJavaClass(dataType), paramDirection == DbParameterAccessor.RETURN_VALUE ? -1 : position++);
				allParams.put(NameNormaliser.normaliseName(paramName), dbp);
			}
			return allParams;
		} finally {
			DBHelper.closeResultSet(rs);
			rs = null;
			DBHelper.closeStatement(dc);
			dc = null;
		}
	}

	private static int getParameterDirection(String direction) {
		if ("P".equals(direction)) {
			return DbParameterAccessor.INPUT;
		}
		if ("O".equals(direction)) {
			return DbParameterAccessor.OUTPUT;
		}
		if ("B".equals(direction)) {
			return DbParameterAccessor.INPUT_OUTPUT;
		}
		if ("C".equals(direction)) {
			return DbParameterAccessor.RETURN_VALUE;
		}
		throw new UnsupportedOperationException("Direction " + direction + " is not supported");
	}

	// List interface has sequential search, so using list instead of array to
	// map types
	private static List<String> stringTypes = Arrays.asList(new String[] { "VARCHAR", "CHAR", "CHARACTER", "GRAPHIC",
			"VARGRAPHIC" });
	private static List<String> intTypes = Arrays.asList(new String[] { "SMALLINT", "INT", "INTEGER" });
	private static List<String> longTypes = Arrays.asList(new String[] { "BIGINT" });
	private static List<String> floatTypes = Arrays.asList(new String[] { "FLOAT" });
	private static List<String> doubleTypes = Arrays.asList(new String[] { "DOUBLE" });
	private static List<String> decimalTypes = Arrays.asList(new String[] { "DECIMAL", "DEC" });
	private static List<String> dateTypes = Arrays.asList(new String[] { "DATE" });
	private static List<String> timestampTypes = Arrays.asList(new String[] { "TIMESTAMP" });

	private static String NormaliseTypeName(String dataType) {
		dataType = dataType.toUpperCase().trim();
		return dataType;
	}

	private static int getSqlType(String dataType) {
		// todo:strip everything from first blank
		dataType = NormaliseTypeName(dataType);

		if (stringTypes.contains(dataType)) {
			return java.sql.Types.VARCHAR;
		}
		if (decimalTypes.contains(dataType)) {
			return java.sql.Types.NUMERIC;
		}
		if (intTypes.contains(dataType)) {
			return java.sql.Types.INTEGER;
		}
		if (floatTypes.contains(dataType)) {
			return java.sql.Types.FLOAT;
		}
		if (doubleTypes.contains(dataType)) {
			return java.sql.Types.DOUBLE;
		}
		if (longTypes.contains(dataType)) {
			return java.sql.Types.BIGINT;
		}
		if (timestampTypes.contains(dataType)) {
			return java.sql.Types.TIMESTAMP;
		}
		if (dateTypes.contains(dataType)) {
			return java.sql.Types.DATE;
		}
		throw new UnsupportedOperationException("Type " + dataType + " is not supported");
	}

	public Class getJavaClass(String dataType) {
		dataType = NormaliseTypeName(dataType);
		if (stringTypes.contains(dataType)) {
			return String.class;
		}
		if (decimalTypes.contains(dataType)) {
			return BigDecimal.class;
		}
		if (intTypes.contains(dataType)) {
			return Integer.class;
		}
		if (floatTypes.contains(dataType)) {
			return Float.class;
		}
		if (dateTypes.contains(dataType)) {
			return java.sql.Date.class;
		}
		if (doubleTypes.contains(dataType)) {
			return Double.class;
		}
		if (longTypes.contains(dataType)) {
			return Long.class;
		}
		if (timestampTypes.contains(dataType)) {
			return java.sql.Timestamp.class;
		}
		throw new UnsupportedOperationException("Type " + dataType + " is not supported");
	}

	public Map<String, DbParameterAccessor> getAllProcedureParameters(String procName) throws SQLException {
		String[] qualifiers = NameNormaliser.normaliseName(procName).split("\\.");
		String qry = " select parmname as column_name, typename as data_type, length, "
				+ "	rowtype as direction, ordinal from SYSIBM.SYSroutinePARMS  where ";
		if (qualifiers.length == 2) {
			qry += " lower(routineschema)=? and lower(routinename)=? ";
		} else {
			qry += " (lower(routinename)=?)";
		}
		qry += " order by ordinal";
		return readIntoParams(qualifiers, qry);
	}

	public String getFieldQuato() {
		return "";
	}
}
