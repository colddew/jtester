package org.jtester.json.decoder.single.spec;

import java.util.concurrent.atomic.AtomicLong;

import org.jtester.json.decoder.single.SpecTypeDecoder;

@SuppressWarnings("rawtypes")
public class AtomicLongDecoder<T extends AtomicLong> extends SpecTypeDecoder<AtomicLong, T> {

	public AtomicLongDecoder(Class clazz) {
		super(clazz);
	}

	@Override
	protected void setTargetValue(AtomicLong target, String value) {
		long newValue = Long.parseLong(value);
		target.set(newValue);
	}

	@Override
	protected AtomicLong getDefaultInstance() {
		return new AtomicLong();
	}
}
