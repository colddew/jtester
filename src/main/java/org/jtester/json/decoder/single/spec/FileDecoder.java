package org.jtester.json.decoder.single.spec;

import java.io.File;

import org.jtester.json.decoder.single.SpecTypeDecoder;
import org.jtester.json.helper.JSONSingle;

@SuppressWarnings("rawtypes")
public class FileDecoder<T extends File> extends SpecTypeDecoder<File, T> {

	public FileDecoder(Class clazz) {
		super(clazz);
	}

	protected File decodeSimpleValue(JSONSingle simple) {
		String value = simple.toStringValue();
		if (value == null) {
			return null;
		} else {
			File file = new File(value);
			return file;
		}
	}

	@Override
	protected void setTargetValue(File target, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	protected File getDefaultInstance() {
		// TODO Auto-generated method stub
		return null;
	}
}
