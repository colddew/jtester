package org.jtester.json.decoder.array;

import java.util.Map;

import org.jtester.json.JSON;
import org.jtester.json.decoder.ArrayDecoder;
import org.jtester.json.helper.JSONObject;

public class CharArrayDecoder extends ArrayDecoder<char[]> {
	public final static CharArrayDecoder instance = new CharArrayDecoder();

	private CharArrayDecoder() {
		super(char.class);
	}

	@Override
	protected char[] newArraysObject(int size) {
		return new char[size];
	}

	@Override
	protected void setObjectByIndex(char[] array, int index, JSONObject json, Map<String, Object> references) {
		char value = (Character) JSON.toObject(json, char.class, references);
		array[index] = value;
	}
}
