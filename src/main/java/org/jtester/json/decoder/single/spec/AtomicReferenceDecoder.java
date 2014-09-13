package org.jtester.json.decoder.single.spec;

import java.util.concurrent.atomic.AtomicReference;

import org.jtester.json.decoder.single.SpecTypeDecoder;

@SuppressWarnings("rawtypes")
public class AtomicReferenceDecoder<T extends AtomicReference> extends SpecTypeDecoder<AtomicReference, T> {

	public AtomicReferenceDecoder(Class clazz) {
		super(clazz);
	}

	@Override
	protected void setTargetValue(AtomicReference target, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	protected AtomicReference getDefaultInstance() {
		// TODO Auto-generated method stub
		return null;
	}
}
