package org.jtester.json.decoder.array;

import java.util.Map;

import org.jtester.json.JSON;
import org.jtester.json.decoder.ArrayDecoder;
import org.jtester.json.helper.JSONObject;

public class IntArrayDecoder extends ArrayDecoder<int[]> {
	public final static IntArrayDecoder instance = new IntArrayDecoder();

	private IntArrayDecoder() {
		super(int.class);
	}

	@Override
	protected int[] newArraysObject(int size) {
		return new int[size];
	}

	@Override
	protected void setObjectByIndex(int[] array, int index, JSONObject json, Map<String, Object> references) {
		int value = (Integer)JSON.toObject(json, int.class, references);
		array[index] = value;
	}
}
