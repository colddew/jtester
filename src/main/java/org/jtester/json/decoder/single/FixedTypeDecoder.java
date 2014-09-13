package org.jtester.json.decoder.single;

import java.util.Map;

import org.jtester.json.decoder.SingleDecoder;
import org.jtester.json.helper.JSONMap;
import org.jtester.json.helper.JSONObject;
import org.jtester.json.helper.JSONSingle;

@SuppressWarnings({ "rawtypes" })
public abstract class FixedTypeDecoder<T> extends SingleDecoder<T> {

	protected FixedTypeDecoder(Class clazz) {
		super(clazz);
	}

	public T decode(JSONObject json, Map<String, Object> references) {
		if (json == null) {
			return null;
		}

		if (json instanceof JSONMap) {
			return this.decodeFromJSONMap((JSONMap) json);
		} else if (json instanceof JSONSingle) {
			T value = this.decodeFromJSONSingle((JSONSingle) json);
			return value;
		} else {
			throw new RuntimeException("syntax error, JSONObject of Single Type can't be JSONArray.");
		}
	}

	protected final T decodeFromJSONSingle(JSONSingle single) {
		String value = single.toStringValue();
		if (value == null || value.length() == 0) {
			return null;
		}
		T object = this.decodeFromString(value);
		return object;
	}

	protected T decodeFromJSONMap(JSONMap map) {
		JSONObject jsonObject = map.getValueFromJSONProp();
		if (jsonObject instanceof JSONSingle) {
			T o = this.decodeFromJSONSingle((JSONSingle) jsonObject);
			return o;
		} else {
			throw new RuntimeException("illegal syntax.");
		}
	}

	protected abstract T decodeFromString(String value);
}
