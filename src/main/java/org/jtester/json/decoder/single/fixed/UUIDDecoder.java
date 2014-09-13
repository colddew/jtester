package org.jtester.json.decoder.single.fixed;

import java.util.UUID;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class UUIDDecoder extends FixedTypeDecoder<UUID> {
	public final static UUIDDecoder instance = new UUIDDecoder();

	private UUIDDecoder() {
		super(UUID.class);
	}

	@Override
	protected UUID decodeFromString(String value) {
		UUID uuid = UUID.fromString(value);
		return uuid;
	}
}
