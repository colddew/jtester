package org.jtester.json.decoder.single.fixed;

import org.jtester.json.decoder.single.FixedTypeDecoder;

@SuppressWarnings("rawtypes")
public class ClazzDecoder extends FixedTypeDecoder<Class> {
	public static final ClazzDecoder instance = new ClazzDecoder();

	private ClazzDecoder() {
		super(Class.class);
	}

	@Override
	protected Class decodeFromString(String value) {
		value = value.trim();
		try {
			Class clazz = Class.forName(value);
			return clazz;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
