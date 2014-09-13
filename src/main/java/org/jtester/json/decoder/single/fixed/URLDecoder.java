package org.jtester.json.decoder.single.fixed;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class URLDecoder extends FixedTypeDecoder<URL> {
	public final static URLDecoder instance = new URLDecoder();

	private URLDecoder() {
		super(URL.class);
	}

	@Override
	protected URL decodeFromString(String value) {
		try {
			URL url = URI.create(value).toURL();
			return url;
		} catch (MalformedURLException e) {
			String message = "the value " + value + " can't cast to URL.";
			throw new RuntimeException(message, e);
		}
	}
}
