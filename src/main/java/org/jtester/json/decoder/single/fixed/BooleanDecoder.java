package org.jtester.json.decoder.single.fixed;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class BooleanDecoder extends FixedTypeDecoder<Boolean> {
	public final static BooleanDecoder instance = new BooleanDecoder();

	private BooleanDecoder() {
		super(Boolean.class);
	}

	@Override
	protected Boolean decodeFromString(String value) {
		value = value.trim();
		return toBoolean(value);
	}

	/**
	 * 将字符串转为布尔值
	 * 
	 * @param value
	 * @return
	 */
	public static boolean toBoolean(String value) {
		if ("true".equalsIgnoreCase(value) || "1".equals(value)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
}
