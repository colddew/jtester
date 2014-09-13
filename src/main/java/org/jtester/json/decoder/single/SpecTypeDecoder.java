package org.jtester.json.decoder.single;

import java.util.Map;

import org.jtester.bytecode.reflector.helper.ClazzHelper;
import org.jtester.json.decoder.SingleDecoder;
import org.jtester.json.helper.JSONMap;
import org.jtester.json.helper.JSONObject;
import org.jtester.json.helper.JSONSingle;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class SpecTypeDecoder<E, T extends E> extends SingleDecoder<T> {

	protected SpecTypeDecoder(Class clazz) {
		super(clazz);
	}

	public T decode(JSONObject json, Map<String, Object> references) {
		if (json == null) {
			return null;
		}

		if (json instanceof JSONMap) {
			return this.decodeFromJSONMap((JSONMap) json, references);
		} else if (json instanceof JSONSingle) {
			T value = this.decodeFromJSONSingle((JSONSingle) json);
			return value;
		} else {
			throw new RuntimeException("syntax error, JSONObject of Single Type can't be JSONArray.");
		}
	}

	private T decodeFromJSONSingle(JSONSingle single) {
		String value = single.toStringValue();
		if (value == null) {
			return null;
		}
		boolean isInterface = ClazzHelper.isInterfaceOrAbstract(this.clazz);
		E target = null;
		if (isInterface) {
			target = this.getDefaultInstance();
		} else {
			target = (E) ClazzHelper.newInstance(this.clazz);
		}
		this.setTargetValue(target, value);
		return (T) target;
	}

	private T decodeFromJSONMap(JSONMap map, Map<String, Object> references) {
		Class clazz = map.getClazzFromJSONFProp(this.clazz);

		JSONObject json = map.getValueFromJSONProp();
		if (!(json instanceof JSONSingle)) {
			throw new RuntimeException(
					"illegal syntax, the JSONObject value of Single Type Object can only be JSONSingle.");
		}
		String value = ((JSONSingle) json).toStringValue();
		T target = (T) ClazzHelper.newInstance(clazz);
		this.setTargetValue(target, value);
		// TODO other properties
		return target;
	}

	protected abstract void setTargetValue(E target, String value);

	protected abstract E getDefaultInstance();
}
