package org.jtester.json.decoder.single.fixed;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class DoubleDecoder extends FixedTypeDecoder<Double> {

	public final static DoubleDecoder instance = new DoubleDecoder();

	private DoubleDecoder() {
		super(Double.class);
	}

	@Override
	protected Double decodeFromString(String value) {
		value = value.trim();
		try {
			value = value.trim();
			if (value.endsWith("d") || value.endsWith("D")) {
				value = value.substring(0, value.length() - 1);
			}
			return Double.parseDouble(value);
		} catch (Exception e) {
			String message = String.format("the value{%s} can't be casted to double number.", value);
			throw new RuntimeException(message, e);
		}
	}
}
