package org.jtester.json.decoder.single.fixed;

import java.util.TimeZone;

import org.jtester.json.decoder.single.FixedTypeDecoder;

public class TimeZoneDecoder extends FixedTypeDecoder<TimeZone> {

	public final static TimeZoneDecoder instance = new TimeZoneDecoder();

	private TimeZoneDecoder() {
		super(TimeZone.class);
	}

	@Override
	protected TimeZone decodeFromString(String value) {
		TimeZone timezone = TimeZone.getTimeZone(value.trim());
		return timezone;
	}
}
