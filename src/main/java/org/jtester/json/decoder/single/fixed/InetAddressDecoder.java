package org.jtester.json.decoder.single.fixed;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class InetAddressDecoder extends FixedTypeDecoder<InetAddress> {
	public static InetAddressDecoder instance = new InetAddressDecoder();

	private InetAddressDecoder() {
		super(InetAddress.class);
	}

	@Override
	protected InetAddress decodeFromString(String host) {
		try {
			return InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			String message = "can't cast value[" + host + "] to InetAddress.";
			throw new RuntimeException(message, e);
		}
	}
}
