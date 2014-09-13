package org.jtester.json.decoder.single.fixed;

import java.net.InetSocketAddress;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class SocketAddressDecoder extends FixedTypeDecoder<InetSocketAddress> {

	public final static SocketAddressDecoder instance = new SocketAddressDecoder();

	private SocketAddressDecoder() {
		super(InetSocketAddress.class);
	}

	@Override
	protected InetSocketAddress decodeFromString(String value) {
		String[] parts = value.split(":");
		if (parts.length == 1) {
			return new InetSocketAddress(parts[0], 0);
		}
		int port = Integer.valueOf(parts[1]);
		return new InetSocketAddress(parts[0], port);
	}
}
