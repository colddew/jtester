package org.jtester.json.decoder.single.spec;

import java.io.IOException;
import java.io.StringWriter;

import org.jtester.bytecode.reflector.helper.ClazzHelper;
import org.jtester.json.decoder.single.SpecTypeDecoder;
import org.jtester.json.helper.JSONSingle;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class AppendableDecoder<T extends Appendable> extends SpecTypeDecoder<Appendable, T> {

	public AppendableDecoder(Class clazz) {
		super(clazz);
	}

	protected T decodeSimpleValue(JSONSingle simple) {
		String value = simple.toStringValue();
		if (value == null) {
			return null;
		}
		T appendable = (T) ClazzHelper.newInstance(this.clazz);
		try {
			appendable.append(value);
			return appendable;
		} catch (IOException e) {
			String message = "can't cast value[" + value + "] to " + this.clazz.getName();
			throw new RuntimeException(message, e);
		}
	}

	@Override
	protected void setTargetValue(Appendable target, String value) {
		try {
			target.append(value);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Appendable getDefaultInstance() {
		return new StringWriter();
	}
}
