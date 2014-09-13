package org.jtester.json.decoder.single.spec;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jtester.bytecode.reflector.helper.ClazzHelper;
import org.jtester.json.decoder.single.SpecTypeDecoder;
import org.jtester.json.helper.JSONSingle;
import org.jtester.utility.DateUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DateDecoder<T extends Date> extends SpecTypeDecoder<Date, T> {

	public DateDecoder(Class clazz) {
		super(clazz);
	}

	protected Date decodeSimpleValue(JSONSingle simple) {
		String value = simple.toStringValue();
		if (value == null) {
			return null;
		}
		T date = (T) ClazzHelper.newInstance(this.clazz);
		long time = getTime(value);
		date.setTime(time);

		return date;
	}

	@Override
	protected void setTargetValue(Date target, String value) {
		long time = getTime(value);
		target.setTime(time);
	}

	@Override
	protected Date getDefaultInstance() {
		return new Date();
	}

	private static long getTime(String value) {
		if (value.matches("\\d+[Ll]?")) {
			long time = Long.valueOf(value.replaceAll("[Ll]", ""));
			return time;
		}
		if (dateFormat == null) {
			Date date = DateUtil.parse(value);
			return date.getTime();
		}
		DateFormat df = new SimpleDateFormat(dateFormat);
		try {
			Date date = df.parse(value);
			return date.getTime();
		} catch (ParseException e) {
			String message = "";
			throw new RuntimeException(message, e);
		}
	}

	private static String dateFormat = null;

	public static void setDateFormat(String format) {
		dateFormat = format;
	}
}
