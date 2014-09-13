package org.jtester.json.decoder.single.spec;

import java.util.concurrent.atomic.AtomicIntegerArray;

import org.jtester.json.decoder.single.SpecTypeDecoder;

@SuppressWarnings("rawtypes")
public class AtomicIntegerArrayDecoder<T extends AtomicIntegerArray> extends SpecTypeDecoder<AtomicIntegerArray, T> {

	public AtomicIntegerArrayDecoder(Class clazz) {
		super(clazz);
	}

	@Override
	protected void setTargetValue(AtomicIntegerArray target, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	protected AtomicIntegerArray getDefaultInstance() {
		// TODO Auto-generated method stub
		return null;
	}
}
