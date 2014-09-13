package org.jtester.json.decoder.single.fixed;

import java.math.BigInteger;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class BigIntegerDecoder extends FixedTypeDecoder<BigInteger> {

	public final static BigIntegerDecoder instance = new BigIntegerDecoder();

	private BigIntegerDecoder() {
		super(BigInteger.class);
	}

	@Override
	protected BigInteger decodeFromString(String value) {
		try {
			value = value.trim();
			return new BigInteger(value);
		} catch (Exception e) {
			String message = String.format("the value{%s} can't be casted to BigInteger.", value);
			throw new RuntimeException(message, e);
		}
	}
}
