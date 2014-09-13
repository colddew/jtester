package org.jtester.json.decoder.single.spec;

import java.util.concurrent.atomic.AtomicInteger;

import org.jtester.json.decoder.single.SpecTypeDecoder;

@SuppressWarnings("rawtypes")
public class AtomicIntegerDecoder<T extends AtomicInteger> extends SpecTypeDecoder<AtomicInteger, T> {

	public AtomicIntegerDecoder(Class clazz) {
		super(clazz);
	}

	@Override
	protected void setTargetValue(AtomicInteger target, String value) {
		int newValue = Integer.parseInt(value);
		target.set(newValue);
	}

	@Override
	protected AtomicInteger getDefaultInstance() {
		return new AtomicInteger();
	}
}
