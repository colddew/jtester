package org.jtester.module.dbfit.db.model;

import java.util.Date;

import org.jtester.utility.DateUtil;

public class SqlDateParseDelegate {

	public static Object parse(String s) throws Exception {
		Date date = DateUtil.parse(s);
		return new java.sql.Date(date.getTime());
	}
}
