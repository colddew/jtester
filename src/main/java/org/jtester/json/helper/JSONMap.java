package org.jtester.json.helper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jtester.exception.JSONException;
import org.jtester.utility.StringHelper;

@SuppressWarnings({ "rawtypes" })
public class JSONMap extends LinkedHashMap<JSONObject, JSONObject> implements JSONObject {

	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_INITIAL_CAPACITY = 16;

	public JSONMap() {
		super(DEFAULT_INITIAL_CAPACITY);
	}

	public JSONMap(int size) {
		super(size);
	}

	public JSONMap(Map<JSONObject, JSONObject> map) {
		super(map);
	}

	@Override
	public boolean containsKey(Object key) {
		JSONObject newkey = JSONSingle.convertJSON(key);
		return super.containsKey(newkey);
	}

	@Override
	public JSONObject get(Object key) {
		JSONObject newkey = JSONSingle.convertJSON(key);
		return super.get(newkey);
	}

	/**
	 * same as put(key,value)
	 * 
	 * @param key
	 * @param value
	 */
	public JSONObject putJSON(Object key, Object value) {
		JSONObject newkey = JSONSingle.convertJSON(key);
		JSONObject newValue = JSONSingle.convertJSON(value);
		return super.put(newkey, newValue);
	}

	private String referenceID = null;

	/**
	 * 返回对象的hashcode值
	 * 
	 * @return
	 */
	public String getReferenceID() {
		return referenceID;
	}

	/**
	 * 返回json对象类型名称<br>
	 * 同时记录对象的hashcode值
	 * 
	 * @param defaultClazz
	 *            默认值
	 * @return
	 */
	public Class getClazzFromJSONFProp(Class defaultClazz) {
		JSONObject object = this.get(JSON_ClazzFlag);
		if (object == null) {
			return defaultClazz;
		} else if (!(object instanceof JSONSingle)) {
			throw new JSONException("the class flag value can only be JSONSingle type.");
		}
		JSONSingle value = (JSONSingle) object;
		String clazzName = value.toClazzName();
		if (StringHelper.isBlankOrNull(clazzName)) {
			this.referenceID = null;
			return defaultClazz;
		} else {
			Class clazz = ClazzMap.getClazzType(clazzName.trim());
			this.referenceID = value.toReferenceID();
			return clazz == null ? defaultClazz : clazz;
		}
	}

	/**
	 * 返回json字符串中value:xxx中xxx表示JSON对象
	 * 
	 * @return
	 */
	public JSONObject getValueFromJSONProp() {
		JSONObject value = this.get(JSON_ValueFlag);
		return value;
	}

	/**
	 * 返回对象的引用地址hascode<br>
	 * 如果json没有记录则返回null
	 * 
	 * @return
	 */
	public String getReferFromJSONProp() {
		JSONObject object = (JSONSingle) this.get(JSON_ReferFlag);
		if (object == null) {
			return null;
		} else if (!(object instanceof JSONSingle)) {
			throw new JSONException("the object reference value can only be JSONSingle type.");
		}

		JSONSingle value = (JSONSingle) object;
		String referenceID = value.toStringValue();
		return referenceID;
	}

	public String description() {
		return toString();
	}
}
