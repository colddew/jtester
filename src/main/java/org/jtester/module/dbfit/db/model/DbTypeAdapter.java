package org.jtester.module.dbfit.db.model;

import org.jtester.fit.util.ParseArg;

import fit.TypeAdapter;

public class DbTypeAdapter extends TypeAdapter {

	public Object parse(final String s) throws Exception {
		String text = ParseArg.parseCellValue(s);
		boolean isNull = text.toLowerCase().equals("<null>");
		if (isNull) {
			return null;
		}

		TypeAdapter ta = TypeAdapter.adapterFor(this.type);
		boolean isTypeAdapter = ta.getClass().equals(TypeAdapter.class);
		if (isTypeAdapter) {
			return super.parse(text);
		} else {
			return ta.parse(text);
		}
	}
}
