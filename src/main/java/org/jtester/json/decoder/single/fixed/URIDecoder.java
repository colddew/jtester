package org.jtester.json.decoder.single.fixed;

import java.net.URI;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class URIDecoder extends FixedTypeDecoder<URI> {
	public final static URIDecoder instance = new URIDecoder();

	private URIDecoder() {
		super(URI.class);
	}

	@Override
	protected URI decodeFromString(String value) {
		URI uri = URI.create(value);
		return uri;
	}
}
