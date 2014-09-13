package org.jtester.json.decoder;

import java.util.HashMap;
import java.util.Map;

import org.jtester.bytecode.reflector.helper.ClazzHelper;
import org.jtester.exception.JSONException;
import org.jtester.json.helper.JSONMap;
import org.jtester.json.helper.JSONObject;
import org.jtester.json.helper.JSONSingle;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class ObjectDecoder<T> extends JSONDecoder<T> {

	protected ObjectDecoder(Class clazz) {
		super(clazz);
	}

	@Override
	public final T decode(JSONObject json, Map<String, Object> references) {
		if (json == null) {
			return null;
		}

		if (json instanceof JSONSingle) {
			String value = ((JSONSingle) json).toStringValue();
			return (T) value;
		}
		if (!(json instanceof JSONMap)) {
			throw new JSONException("illegal type for JavaBeanDecoder. the json[" + json.toString()
					+ "] isn't a JSONMap");
		}
		JSONMap map = (JSONMap) json;
		String referenceID = map.getReferFromJSONProp();
		if (referenceID != null) {
			Object o = references.get(referenceID);
			return (T) o;
		} else {
			T target = this.newInstance(map, references);
			this.parseFromJSONMap(target, map, references);
			return (T) target;
		}
	}

	/**
	 * 根据JSONMap在#class属性或者 Decoder传进来的class创建对象实例<br>
	 * 如果实例被标记了ReferenceID，往references中追加一条记录
	 * 
	 * @param map
	 * @param references
	 * @return
	 */
	private T newInstance(final JSONMap map, final Map<String, Object> references) {
		this.realTargetType = map.getClazzFromJSONFProp(this.clazz);
		if (ClazzHelper.isInterfaceOrAbstract(this.realTargetType)) {
			throw new JSONException("the type[" + this.realTargetType.getName()
					+ "] is an interface or abstract class,that can't be instnaced.");
		}
		if (this.clazz == Object.class) {
			Object target = new HashMap();
			return (T) target;
		} else {
			Object target = ClazzHelper.newInstance(this.realTargetType);
			String referenceID = map.getReferenceID();
			if (referenceID != null) {
				references.put(referenceID, target);
			}
			return (T) target;
		}
	}

	/**
	 * 解析jsonMap属性，填充target对象的值
	 * 
	 * @param target
	 * @param jsonMap
	 * @param references
	 * @return
	 */
	protected abstract void parseFromJSONMap(T target, JSONMap jsonMap, Map<String, Object> references);
}
