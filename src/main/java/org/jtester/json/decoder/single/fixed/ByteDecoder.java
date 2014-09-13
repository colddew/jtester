package org.jtester.json.decoder.single.fixed;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class ByteDecoder extends FixedTypeDecoder<Byte> {
	public final static ByteDecoder instance = new ByteDecoder();

	private ByteDecoder() {
		super(Byte.class);
	}

	@Override
	protected Byte decodeFromString(String value) {
		try {
			return Byte.parseByte(value);
		} catch (Exception e) {
			String message = String.format("the value{%s} can't be casted to byte.", value);
			throw new RuntimeException(message, e);
		}
	}
}
