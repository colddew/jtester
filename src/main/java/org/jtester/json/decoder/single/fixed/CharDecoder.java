package org.jtester.json.decoder.single.fixed;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class CharDecoder extends FixedTypeDecoder<Character> {
	public final static CharDecoder instance = new CharDecoder();

	private CharDecoder() {
		super(Character.class);
	}

	@Override
	protected Character decodeFromString(String value) {
		if (value.length() != 1) {
			String message = String.format("the value{%s} can't be casted to byte.", value);
			throw new RuntimeException(message);
		} else {
			return value.charAt(0);
		}
	}
}
