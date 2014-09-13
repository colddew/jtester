package org.jtester.json.decoder.array;

import java.util.Map;

import org.jtester.json.JSON;
import org.jtester.json.decoder.ArrayDecoder;
import org.jtester.json.helper.JSONObject;

public class ByteArrayDecoder extends ArrayDecoder<byte[]> {
	public final static ByteArrayDecoder instance = new ByteArrayDecoder();

	private ByteArrayDecoder() {
		super(byte.class);
	}

	@Override
	protected byte[] newArraysObject(int size) {
		return new byte[size];
	}

	@Override
	protected void setObjectByIndex(byte[] array, int index, JSONObject json, Map<String, Object> references) {
		byte value = (Byte)JSON.toObject(json, byte.class, references);
		array[index] = value;
	}
}
