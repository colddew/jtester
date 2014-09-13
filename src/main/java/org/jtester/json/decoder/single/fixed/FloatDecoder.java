package org.jtester.json.decoder.single.fixed;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class FloatDecoder extends FixedTypeDecoder<Float> {
	public final static FloatDecoder instance = new FloatDecoder();

	private FloatDecoder() {
		super(Float.class);
	}

	@Override
	protected Float decodeFromString(String value) {
		try {
			value = value.trim();
			if (value.endsWith("f") || value.endsWith("F")) {
				value = value.substring(0, value.length() - 1);
			}
			return Float.parseFloat(value);
		} catch (Exception e) {
			String message = String.format("the value{%s} can't be casted to float number.", value);
			throw new RuntimeException(message, e);
		}
	}
}
