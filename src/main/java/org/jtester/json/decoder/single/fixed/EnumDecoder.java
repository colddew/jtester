package org.jtester.json.decoder.single.fixed;

import org.jtester.json.decoder.single.FixedTypeDecoder;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EnumDecoder<T extends Enum> extends FixedTypeDecoder<T> {
	public EnumDecoder(Class clazz) {
		super(clazz);
	}

	@Override
	protected T decodeFromString(String value) {
		Enum enumValue = Enum.valueOf(this.clazz, value);
		return (T) enumValue;
	}
}
