package org.jtester.json.decoder.single.spec;

import java.util.concurrent.atomic.AtomicLongArray;

import org.jtester.json.decoder.single.SpecTypeDecoder;

@SuppressWarnings("rawtypes")
public class AtomicLongArrayDecoder<T extends AtomicLongArray> extends SpecTypeDecoder<AtomicLongArray, T> {

	public AtomicLongArrayDecoder(Class clazz) {
		super(clazz);
	}

	@Override
	protected void setTargetValue(AtomicLongArray target, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	protected AtomicLongArray getDefaultInstance() {
		// TODO Auto-generated method stub
		return null;
	}
}
