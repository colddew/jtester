package org.jtester.json.decoder.single.fixed;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class StringDecoder extends FixedTypeDecoder<String> {
	public static StringDecoder instance = new StringDecoder();

	private StringDecoder() {
		super(String.class);
	}

	@Override
	protected String decodeFromString(String value) {
		return value;
	}
}
