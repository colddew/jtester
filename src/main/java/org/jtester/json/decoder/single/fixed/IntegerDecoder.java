package org.jtester.json.decoder.single.fixed;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class IntegerDecoder extends FixedTypeDecoder<Integer> {
	public final static IntegerDecoder instance = new IntegerDecoder();

	protected IntegerDecoder() {
		super(Integer.class);
	}

	@Override
	protected Integer decodeFromString(String value) {
		try {
			value = value.trim();
			return Integer.parseInt(value);
		} catch (Exception e) {
			String message = String.format("the value{%s} can't be casted to integer number.", value);
			throw new RuntimeException(message, e);
		}
	}
}
