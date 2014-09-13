package org.jtester.json.decoder.single.fixed;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class ShortDecoder extends FixedTypeDecoder<Short> {

	public final static ShortDecoder instance = new ShortDecoder();

	private ShortDecoder() {
		super(Short.class);
	}

	@Override
	protected Short decodeFromString(String value) {
		try {
			value = value.trim();
			return Short.parseShort(value);
		} catch (Exception e) {
			String message = String.format("the value{%s} can't be casted to short number.", value);
			throw new RuntimeException(message, e);
		}
	}
}
