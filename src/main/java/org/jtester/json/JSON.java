package org.jtester.json;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jtester.json.decoder.JSONDecoder;
import org.jtester.json.encoder.JSONEncoder;
import org.jtester.json.helper.JSONArray;
import org.jtester.json.helper.JSONFeature;
import org.jtester.json.helper.JSONMap;
import org.jtester.json.helper.JSONObject;
import org.jtester.json.helper.JSONScanner;
import org.jtester.json.helper.JSONSingle;

/**
 * json解码，编码工具类
 * 
 * @author darui.wudr
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class JSON {

	/**
	 * 将json字符串反序列为对象
	 * 
	 * @param json
	 * @return
	 */
	public static final <T> T toObject(String json) {
		if (json == null) {
			return null;
		}
		JSONObject jsonObject = JSONScanner.scnJSON(json);

		Object o = toObject(jsonObject, new HashMap<String, Object>());
		return (T) o;
	}

	public static final <T> T toObject(String json, Class clazz) {
		if (json == null) {
			return null;
		}
		JSONObject jsonObject = JSONScanner.scnJSON(json);

		Object o = toObject(jsonObject, clazz, new HashMap<String, Object>());
		return (T) o;
	}

	/**
	 * 将json字符串反序列为对象
	 * 
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @param decodingFeatures
	 * @return
	 */

	public static final <T> T toObject(JSONObject json, Map<String, Object> references) {
		if (json instanceof JSONArray) {
			Object o = toObject(json, Object[].class, references);
			return (T) o;
		} else if (json instanceof JSONMap) {
			Class clazz = ((JSONMap) json).getClazzFromJSONFProp(HashMap.class);
			Object o = toObject(json, clazz, references);
			return (T) o;
		} else {
			String value = ((JSONSingle) json).toStringValue();
			return (T) value;
		}
	}

	public static final <T> T toObject(JSONObject json, Class clazz, Map<String, Object> references) {
		if (clazz == null) {
			throw new RuntimeException("the decode class can't be null.");
		}
		JSONDecoder decoder = JSONDecoder.get(clazz);
		Object obj = decoder.decode(json, references);
		return (T) obj;
	}

	/**
	 * 将对象编码为json串
	 * 
	 * @param object
	 * @return
	 */
	public static final String toJSON(Object object, JSONFeature... features) {
		if (object == null) {
			return "null";
		}
		int value = JSONFeature.getFeaturesMask(features);
		String json = toJSON(object, value);
		return json;
	}

	/**
	 * 将对象编码为json串
	 * 
	 * @param object
	 * @param features
	 * @return
	 */
	public static final String toJSON(Object object, int features) {
		if (object == null) {
			return "null";
		}

		StringWriter writer = new StringWriter();
		JSONEncoder encoder = JSONEncoder.get(object.getClass());
		encoder.setFeatures(features);

		List<String> references = new ArrayList<String>();
		encoder.encode(object, writer, references);
		String json = writer.toString();
		return json;
	}
}
