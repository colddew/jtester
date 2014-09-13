package org.jtester.json.decoder.array;

import java.lang.reflect.Array;
import java.util.Map;

import org.jtester.json.JSON;
import org.jtester.json.decoder.ArrayDecoder;
import org.jtester.json.helper.JSONObject;

@SuppressWarnings({ "rawtypes" })
public class ArraysDecoder extends ArrayDecoder<Object[]> {

	public ArraysDecoder(Class clazz) {
		super(clazz);
	}

	@Override
	protected void setObjectByIndex(Object[] array, int index, JSONObject json, Map<String, java.lang.Object> references) {
		Object value = JSON.toObject(json, this.clazz, references);
		array[index] = value;
	}

	@Override
	protected Object[] newArraysObject(int size) {
		if (this.clazz == Object.class) {
			return new Object[size];
		} else {
			Object[] array = (Object[]) Array.newInstance(this.realTargetType == null ? this.clazz
					: this.realTargetType, size);
			return array;
		}
	}
}
