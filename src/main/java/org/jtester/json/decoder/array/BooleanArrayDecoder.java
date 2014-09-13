package org.jtester.json.decoder.array;

import java.util.Map;

import org.jtester.json.JSON;
import org.jtester.json.decoder.ArrayDecoder;
import org.jtester.json.helper.JSONObject;

public class BooleanArrayDecoder extends ArrayDecoder<boolean[]> {
	public final static BooleanArrayDecoder instance = new BooleanArrayDecoder();

	private BooleanArrayDecoder() {
		super(boolean.class);
	}

	@Override
	protected boolean[] newArraysObject(int size) {
		return new boolean[size];
	}

	@Override
	protected void setObjectByIndex(boolean[] array, int index, JSONObject json, Map<String, Object> references) {
		boolean value = (Boolean) JSON.toObject(json, boolean.class, references);
		array[index] = value;
	}
}
