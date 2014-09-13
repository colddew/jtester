package org.jtester.json.decoder.object;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jtester.json.JSON;
import org.jtester.json.decoder.ObjectDecoder;
import org.jtester.json.helper.JSONMap;
import org.jtester.json.helper.JSONObject;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapDecoder extends ObjectDecoder<Map> {

	public MapDecoder(Class clazz) {
		super(clazz);
	}

	public MapDecoder() {
		super(HashMap.class);
	}

	@Override
	protected void parseFromJSONMap(Map target, JSONMap jsonMap, Map<String, Object> references) {
		for (Iterator<JSONObject> iterator = jsonMap.keySet().iterator(); iterator.hasNext();) {
			JSONObject jsonkey = iterator.next();

			if (jsonkey.equals(JSONMap.JSON_ClazzFlag)) {
				continue;
			}
			Object key = JSON.toObject(jsonkey, references);
			JSONObject jsonvalue = jsonMap.get(jsonkey);
			Object value = JSON.toObject(jsonvalue, references);
			target.put(key, value);
		}
	}
}
