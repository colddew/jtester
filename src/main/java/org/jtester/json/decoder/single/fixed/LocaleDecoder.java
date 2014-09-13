package org.jtester.json.decoder.single.fixed;

import java.util.Locale;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class LocaleDecoder extends FixedTypeDecoder<Locale> {
	public final static LocaleDecoder instance = new LocaleDecoder();

	private LocaleDecoder() {
		super(Locale.class);
	}

	@Override
	protected Locale decodeFromString(String value) {
		String[] parts = value.split("_");

		if (parts.length == 1) {
			return new Locale(parts[0]);
		}

		if (parts.length == 2) {
			return new Locale(parts[0], parts[1]);
		}

		return new Locale(parts[0], parts[1], parts[2]);
	}
}
