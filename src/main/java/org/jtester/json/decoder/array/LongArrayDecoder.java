package org.jtester.json.decoder.array;

import java.util.Map;

import org.jtester.json.JSON;
import org.jtester.json.decoder.ArrayDecoder;
import org.jtester.json.helper.JSONObject;

public class LongArrayDecoder extends ArrayDecoder<long[]> {
	public final static LongArrayDecoder instance = new LongArrayDecoder();

	private LongArrayDecoder() {
		super(long.class);
	}

	@Override
	protected long[] newArraysObject(int size) {
		return new long[size];
	}

	@Override
	protected void setObjectByIndex(long[] array, int index, JSONObject json, Map<String, Object> references) {
		long value = (Long)JSON.toObject(json, long.class, references);
		array[index] = value;
	}
}
