package org.jtester.json.decoder.array;

import java.util.Map;

import org.jtester.json.JSON;
import org.jtester.json.decoder.ArrayDecoder;
import org.jtester.json.helper.JSONObject;

public class FloatArrayDecoder extends ArrayDecoder<float[]> {
	public final static FloatArrayDecoder instance = new FloatArrayDecoder();

	private FloatArrayDecoder() {
		super(float.class);
	}

	@Override
	protected float[] newArraysObject(int size) {
		return new float[size];
	}

	@Override
	protected void setObjectByIndex(float[] array, int index, JSONObject json, Map<String, Object> references) {
		float value = (Float)JSON.toObject(json, float.class, references);
		array[index] = value;
	}
}
