package org.jtester.json.decoder.single.spec;

import java.util.concurrent.atomic.AtomicReferenceArray;

import org.jtester.json.decoder.single.SpecTypeDecoder;

@SuppressWarnings("rawtypes")
public class AtomicReferenceArrayDecoder<T extends AtomicReferenceArray> extends
		SpecTypeDecoder<AtomicReferenceArray, T> {

	public AtomicReferenceArrayDecoder(Class clazz) {
		super(clazz);
	}

	@Override
	protected void setTargetValue(AtomicReferenceArray target, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	protected AtomicReferenceArray getDefaultInstance() {
		// TODO Auto-generated method stub
		return null;
	}
}
