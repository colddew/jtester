package org.jtester.json.decoder.single.spec;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jtester.json.decoder.single.SpecTypeDecoder;
import org.jtester.json.decoder.single.fixed.BooleanDecoder;

@SuppressWarnings("rawtypes")
public class AtomicBooleanDecoder<T extends AtomicBoolean> extends SpecTypeDecoder<AtomicBoolean, T> {

	public AtomicBooleanDecoder(Class clazz) {
		super(clazz);
	}

	@Override
	protected void setTargetValue(AtomicBoolean target, String value) {
		boolean bool = BooleanDecoder.toBoolean(value);
		target.set(bool);
	}

	@Override
	protected AtomicBoolean getDefaultInstance() {
		return new AtomicBoolean();
	}
}
