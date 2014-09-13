package org.jtester.json.decoder.single.fixed;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class LongDecoder extends FixedTypeDecoder<Long> {

	public final static LongDecoder instance = new LongDecoder();

	private LongDecoder() {
		super(Long.class);
	}

	@Override
	protected Long decodeFromString(String value) {
		try {
			value = value.trim();
			if (value.endsWith("l") || value.endsWith("L")) {
				value = value.substring(0, value.length() - 1);
			}
			return Long.parseLong(value);
		} catch (Exception e) {
			String message = String.format("the value{%s} can't be casted to long number.", value);
			throw new RuntimeException(message, e);
		}
	}
}
