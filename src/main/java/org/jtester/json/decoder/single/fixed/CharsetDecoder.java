package org.jtester.json.decoder.single.fixed;

import java.nio.charset.Charset;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class CharsetDecoder extends FixedTypeDecoder<Charset> {

	public final static CharsetDecoder instance = new CharsetDecoder();

	private CharsetDecoder() {
		super(Charset.class);
	}

	@Override
	protected Charset decodeFromString(String value) {
		Charset charset = Charset.forName(value);
		return charset;
	}
}
