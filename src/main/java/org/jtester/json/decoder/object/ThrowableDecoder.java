package org.jtester.json.decoder.object;

import java.util.Map;

import org.jtester.json.decoder.ObjectDecoder;
import org.jtester.json.helper.JSONMap;

public class ThrowableDecoder<T extends Throwable> extends ObjectDecoder<T> {

	@SuppressWarnings("rawtypes")
	public ThrowableDecoder(Class clazz) {
		super(clazz);
	}

	public static final StackTraceElement castToStackTraceElement(Map<String, Object> map) {
		String declaringClass = (String) map.get("className");
		String methodName = (String) map.get("methodName");
		String fileName = (String) map.get("fileName");
		int lineNumber;
		{
			Number value = (Number) map.get("lineNumber");
			if (value == null) {
				lineNumber = 0;
			} else {
				lineNumber = value.intValue();
			}
		}

		return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
	}

	@Override
	protected void parseFromJSONMap(T target, JSONMap jsonMap, Map<String, Object> references) {
		// TODO Auto-generated method stub

	}
}
