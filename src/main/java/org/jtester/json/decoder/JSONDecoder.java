package org.jtester.json.decoder;

import java.util.Collection;
import java.util.Map;

import org.jtester.json.decoder.array.CollectionDecoder;
import org.jtester.json.decoder.object.MapDecoder;
import org.jtester.json.decoder.object.PoJoDecoder;
import org.jtester.json.decoder.single.FixedTypeDecoder;
import org.jtester.json.decoder.single.SpecTypeDecoder;
import org.jtester.json.helper.JSONObject;

/**
 * json串解码器基类<br>
 * 解码：从json字符串反序列为java对象<br>
 * 加码：将java对象序列化为json字符串<br>
 * 
 * @author darui.wudr
 * 
 */
@SuppressWarnings({ "rawtypes" })
public abstract class JSONDecoder<T> {
	/**
	 * 构造decoder时声明的对象class类型
	 */
	protected final Class clazz;

	/**
	 * 真正对象的class类型，等于clazz或者是其子类
	 */
	protected Class realTargetType;

	protected JSONDecoder(Class clazz) {
		this.clazz = clazz;
		this.realTargetType = clazz;
	}

	/**
	 * 从json字符串反序列为java对象
	 * 
	 * @param json
	 * @return
	 */
	public abstract T decode(JSONObject json, Map<String, Object> references);

	public static JSONDecoder get(Class clazz) {
		FixedTypeDecoder decoder = FixedTypeDecoder.isFinalDecoder(clazz);
		if (decoder != null) {
			return decoder;
		}

		SpecTypeDecoder specTypeDecoder = SpecTypeDecoder.isSpecTypeDecoder(clazz);
		if (specTypeDecoder != null) {
			return specTypeDecoder;
		}
		// 数组类型
		if (clazz.isArray()) {
			return ArrayDecoder.getObjectArrayDecoder(clazz.getComponentType());
		}
		// 集合类型
		if (Collection.class.isAssignableFrom(clazz)) {
			return new CollectionDecoder(clazz);
		}
		// Map类型
		if (Map.class.isAssignableFrom(clazz) || clazz == Object.class) {
			return new MapDecoder(clazz);
		}
		// 普通对象
		return new PoJoDecoder(clazz);
	}
}
