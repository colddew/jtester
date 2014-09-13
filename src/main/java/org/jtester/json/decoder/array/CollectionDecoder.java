package org.jtester.json.decoder.array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.jtester.bytecode.reflector.helper.ClazzHelper;
import org.jtester.exception.JSONException;
import org.jtester.json.JSON;
import org.jtester.json.decoder.ArrayDecoder;
import org.jtester.json.helper.JSONObject;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CollectionDecoder extends ArrayDecoder<Collection> {

	public CollectionDecoder(Class clazz) {
		super(clazz);
	}

	public CollectionDecoder() {
		super(ArrayList.class);
	}

	@Override
	protected Collection newArraysObject(int size) {
		if (ClazzHelper.isInterfaceOrAbstract(this.realTargetType)) {
			throw new JSONException("the type[" + this.realTargetType.getName()
					+ "] is an interface or abstract class,that can't be instnaced.");
		}
		Collection target = (Collection) ClazzHelper.newInstance(this.realTargetType == null ? this.clazz
				: this.realTargetType);
		return target;
	}

	@Override
	protected void setObjectByIndex(Collection target, int index, JSONObject json, Map<String, Object> references) {
		Object value = JSON.toObject(json, references);
		target.add(value);
	}
}
