package org.jtester.module.dbfit.db.model;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jtester.module.database.environment.normalise.TypeNormaliser;
import org.jtester.module.database.environment.normalise.TypeNormaliserFactory;

import fit.Fixture;

@SuppressWarnings({ "rawtypes" })
public class DbParameterAccessor extends DbTypeAdapter {
	public static final int RETURN_VALUE = 0;
	public static final int INPUT = 1;
	public static final int OUTPUT = 2;
	public static final int INPUT_OUTPUT = 3;
	public static final int SEQUENCE = 4;

	private int index; // index in effective sql statement (not necessarily the
	// same as position below)
	private int direction;
	private String name;
	private String placeholder = "?";// default insert place holder
	private int sqlType;
	private int position; // zero-based index of parameter in procedure or

	// column in table

	public static Object normaliseValue(Object currVal) throws Exception {
		if (currVal == null) {
			return null;
		}
		TypeNormaliser tn = TypeNormaliserFactory.getNormaliser(currVal.getClass());
		if (tn != null) {
			currVal = tn.normalise(currVal);
		}
		return currVal;
	}

	public DbParameterAccessor(DbParameterAccessor acc) {
		this.name = acc.name;
		this.direction = acc.direction;
		this.sqlType = acc.sqlType;
		this.type = acc.type;
		this.position = acc.position;
	}

	public DbParameterAccessor(String name, int direction, int sqlType, Class javaType, int position) {
		this.name = name;
		this.direction = direction;
		this.sqlType = sqlType;
		this.type = javaType;
		this.position = position;
	}

	public int getSqlType() {
		return sqlType;
	}

	/**
	 * One of the constants from this class declaring whether the param is
	 * input, output or a return value. JDBC does not have a return value
	 * parameter directions, so a new constant list had to be introduced public
	 * static final int RETURN_VALUE=0; public static final int INPUT=1; public
	 * static final int OUTPUT=2; public static final int INPUT_OUTPUT=3;
	 */
	public int getDirection() {
		return direction;
	}

	public String getName() {
		return name;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * prepareStament语句的占位符，默认值是"?"
	 * 
	 * @return
	 */
	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	// really ugly, but a hack to support mysql, because it will not execute
	// inserts with a callable statement
	private CallableStatement convertStatementToCallable() throws SQLException {
		if (cs instanceof CallableStatement) {
			return (CallableStatement) cs;
		}
		throw new SQLException("This operation requires a callable statement instead of " + cs.getClass().getName());
	}

	/*******************************************/
	private PreparedStatement cs;

	public void bindTo(Fixture f, PreparedStatement cs, int ind) throws SQLException {
		this.cs = cs;
		this.fixture = f;
		this.index = ind;
		boolean tocall = direction == DbParameterAccessor.OUTPUT || direction == DbParameterAccessor.RETURN_VALUE
				|| direction == DbParameterAccessor.INPUT_OUTPUT;
		if (tocall) {
			convertStatementToCallable().registerOutParameter(ind, getSqlType());
		}
	}

	public void set(Object value) throws Exception {
		if (direction == OUTPUT || direction == RETURN_VALUE) {
			throw new UnsupportedOperationException("Trying to set value of output parameter " + name);
		}
		if (value instanceof InputStream) {
			InputStream is = (InputStream) value;
			cs.setBinaryStream(index, is, is.available());
		} else {
			cs.setObject(index, value);
		}
	}

	public Object get() throws IllegalAccessException, InvocationTargetException {
		try {
			if (direction == INPUT) {
				String err = "Trying to get value of input parameter " + name;
				throw new UnsupportedOperationException(err);
			}
			CallableStatement statment = convertStatementToCallable();
			Object o = statment.getObject(index);
			return normaliseValue(o);
		} catch (Exception sqle) {
			throw new InvocationTargetException(sqle);
		}
	}

	/**
	 * Zero-based column or parameter position in a query, table or stored proc
	 */
	public int getPosition() {
		return position;
	}

}
