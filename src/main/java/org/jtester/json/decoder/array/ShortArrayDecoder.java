package org.jtester.json.decoder.array;

import java.util.Map;

import org.jtester.json.JSON;
import org.jtester.json.decoder.ArrayDecoder;
import org.jtester.json.helper.JSONObject;

public class ShortArrayDecoder extends ArrayDecoder<short[]> {
	public final static ShortArrayDecoder instance = new ShortArrayDecoder();

	private ShortArrayDecoder() {
		super(short.class);
	}

	@Override
	protected short[] newArraysObject(int size) {
		return new short[size];
	}

	@Override
	protected void setObjectByIndex(short[] array, int index, JSONObject json, Map<String, Object> references) {
		short value = (Short)JSON.toObject(json, short.class, references);
		array[index] = value;
	}
}
