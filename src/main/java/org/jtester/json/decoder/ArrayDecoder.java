package org.jtester.json.decoder;

import java.util.Iterator;
import java.util.Map;

import org.jtester.exception.JSONException;
import org.jtester.json.decoder.array.ArraysDecoder;
import org.jtester.json.decoder.array.BooleanArrayDecoder;
import org.jtester.json.decoder.array.ByteArrayDecoder;
import org.jtester.json.decoder.array.CharArrayDecoder;
import org.jtester.json.decoder.array.DoubleArrayDecoder;
import org.jtester.json.decoder.array.FloatArrayDecoder;
import org.jtester.json.decoder.array.IntArrayDecoder;
import org.jtester.json.decoder.array.LongArrayDecoder;
import org.jtester.json.decoder.array.ShortArrayDecoder;
import org.jtester.json.helper.JSONArray;
import org.jtester.json.helper.JSONMap;
import org.jtester.json.helper.JSONObject;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class ArrayDecoder<T> extends JSONDecoder<T> {

	public ArrayDecoder(Class clazz) {
		super(clazz);
	}

	@Override
	public final T decode(JSONObject json, Map<String, Object> references) {
		if (json == null) {
			return null;
		}
		if (json instanceof JSONMap) {
			return this.parseFromJSONMap((JSONMap) json, references);
		} else if (json instanceof JSONArray) {
			int size = ((JSONArray) json).size();
			T target = this.newArraysObject(size);
			this.parseFromJSONArray(target, ((JSONArray) json), references);
			return target;
		} else {
			throw new RuntimeException(
					"illegal type for ArrayDecoder. the type can only is JSONArray or JSONMap, but actual is JSONSingle.");
		}
	}

	private T parseFromJSONMap(JSONMap map, Map<String, Object> references) {
		String referenceID = map.getReferFromJSONProp();
		if (referenceID != null) {
			Object o = references.get(referenceID);
			return (T) o;
		}

		Class type = map.getClazzFromJSONFProp(this.clazz);
		if (type == null) {
			throw new JSONException("JSONMap must have property that declared the array type.");
		} else if (type.isArray() == false) {
			this.realTargetType = type;
		} else {
			this.realTargetType = type.getComponentType();
		}

		JSONObject array = map.getValueFromJSONProp();
		if (!(array instanceof JSONArray)) {
			throw new JSONException("illegal type for ArrayDecoder. the type can only be JSONArray, but actual is "
					+ array.getClass().getName());
		}
		T target = this.newArraysObject(((JSONArray) array).size());
		referenceID = map.getReferenceID();
		if (referenceID != null) {
			references.put(referenceID, target);
		}
		this.parseFromJSONArray(target, ((JSONArray) array), references);
		return target;
	}

	private final void parseFromJSONArray(T target, JSONArray jsonArray, Map<String, Object> references) {
		int index = 0;
		for (Iterator<JSONObject> it = jsonArray.iterator(); it.hasNext();) {
			JSONObject jsonObject = it.next();
			this.setObjectByIndex(target, index, jsonObject, references);
			index++;
		}
	}

	protected abstract void setObjectByIndex(T array, int index, JSONObject json, Map<String, Object> references);

	protected abstract T newArraysObject(int size);

	public static ArrayDecoder getObjectArrayDecoder(Class type) {
		if (type == char.class) {// char
			return CharArrayDecoder.instance;
		}
		if (type == boolean.class) {// boolean
			return BooleanArrayDecoder.instance;
		}
		if (type == byte.class) {// byte
			return ByteArrayDecoder.instance;
		}
		if (type == short.class) {// short
			return ShortArrayDecoder.instance;
		}
		if (type == int.class) {// int
			return IntArrayDecoder.instance;
		}
		if (type == long.class) {// long
			return LongArrayDecoder.instance;
		}
		if (type == float.class) {// float
			return FloatArrayDecoder.instance;
		}
		if (type == double.class) {// double
			return DoubleArrayDecoder.instance;
		}
		return new ArraysDecoder(type);
	}
}
