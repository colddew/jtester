package org.jtester.json.decoder.single.fixed;

import java.math.BigDecimal;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class BigDecimalDecoder extends FixedTypeDecoder<BigDecimal> {

	public final static BigDecimalDecoder instance = new BigDecimalDecoder();

	private BigDecimalDecoder() {
		super(BigDecimal.class);
	}

	@Override
	protected BigDecimal decodeFromString(String value) {
		try {
			value = value.trim();
			return new BigDecimal(value);
		} catch (Exception e) {
			String message = String.format("the value{%s} can't be casted to BigDecimal.", value);
			throw new RuntimeException(message, e);
		}
	}
}
