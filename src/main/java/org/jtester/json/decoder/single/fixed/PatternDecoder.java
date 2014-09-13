package org.jtester.json.decoder.single.fixed;

import java.util.regex.Pattern;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class PatternDecoder extends FixedTypeDecoder<Pattern> {
	public final static PatternDecoder instance = new PatternDecoder();

	private PatternDecoder() {
		super(Pattern.class);
	}

	@Override
	protected Pattern decodeFromString(String value) {
		Pattern pattern = Pattern.compile(value);
		return pattern;
	}
}
