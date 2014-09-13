package org.jtester.json.decoder.single.spec;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.jtester.bytecode.reflector.helper.FieldHelper;
import org.jtester.bytecode.reflector.helper.MethodHelper;
import org.jtester.exception.JTesterException;
import org.jtester.json.decoder.single.SpecTypeDecoder;

@SuppressWarnings("rawtypes")
public class SimpleDateFormatDecoder<T extends SimpleDateFormat> extends SpecTypeDecoder<SimpleDateFormat, T> {

	public SimpleDateFormatDecoder(Class clazz) {
		super(clazz);
	}

	@Override
	protected void setTargetValue(SimpleDateFormat target, String value) {
		FieldHelper.setFieldValue(target, "pattern", value);
		Locale locale = Locale.getDefault();
		target.setDateFormatSymbols(new DateFormatSymbols(locale));
		try {
			MethodHelper.invoke(target, "initialize", locale);
		} catch (Exception e) {
			throw new JTesterException("Unable to invoke method[initialize].", e);
		}
	}

	@Override
	protected SimpleDateFormat getDefaultInstance() {
		return new SimpleDateFormat();
	}
}
