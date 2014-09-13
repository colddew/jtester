package org.jtester.json.decoder;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.regex.Pattern;

import org.jtester.json.decoder.single.FixedTypeDecoder;
import org.jtester.json.decoder.single.SpecTypeDecoder;
import org.jtester.json.decoder.single.fixed.BigDecimalDecoder;
import org.jtester.json.decoder.single.fixed.BigIntegerDecoder;
import org.jtester.json.decoder.single.fixed.BooleanDecoder;
import org.jtester.json.decoder.single.fixed.ByteDecoder;
import org.jtester.json.decoder.single.fixed.CharDecoder;
import org.jtester.json.decoder.single.fixed.CharsetDecoder;
import org.jtester.json.decoder.single.fixed.ClazzDecoder;
import org.jtester.json.decoder.single.fixed.DoubleDecoder;
import org.jtester.json.decoder.single.fixed.EnumDecoder;
import org.jtester.json.decoder.single.fixed.FloatDecoder;
import org.jtester.json.decoder.single.fixed.InetAddressDecoder;
import org.jtester.json.decoder.single.fixed.IntegerDecoder;
import org.jtester.json.decoder.single.fixed.LocaleDecoder;
import org.jtester.json.decoder.single.fixed.LongDecoder;
import org.jtester.json.decoder.single.fixed.PatternDecoder;
import org.jtester.json.decoder.single.fixed.ShortDecoder;
import org.jtester.json.decoder.single.fixed.SocketAddressDecoder;
import org.jtester.json.decoder.single.fixed.StringDecoder;
import org.jtester.json.decoder.single.fixed.TimeZoneDecoder;
import org.jtester.json.decoder.single.fixed.URIDecoder;
import org.jtester.json.decoder.single.fixed.URLDecoder;
import org.jtester.json.decoder.single.fixed.UUIDDecoder;
import org.jtester.json.decoder.single.spec.AppendableDecoder;
import org.jtester.json.decoder.single.spec.AtomicBooleanDecoder;
import org.jtester.json.decoder.single.spec.AtomicIntegerArrayDecoder;
import org.jtester.json.decoder.single.spec.AtomicIntegerDecoder;
import org.jtester.json.decoder.single.spec.AtomicLongArrayDecoder;
import org.jtester.json.decoder.single.spec.AtomicLongDecoder;
import org.jtester.json.decoder.single.spec.AtomicReferenceArrayDecoder;
import org.jtester.json.decoder.single.spec.AtomicReferenceDecoder;
import org.jtester.json.decoder.single.spec.DateDecoder;
import org.jtester.json.decoder.single.spec.FileDecoder;
import org.jtester.json.decoder.single.spec.SimpleDateFormatDecoder;

@SuppressWarnings({ "rawtypes" })
public abstract class SingleDecoder<T> extends JSONDecoder<T> {

	protected SingleDecoder(Class clazz) {
		super(clazz);
	}

	/**
	 * 是否是简单对象<br>
	 * String<br>
	 * 
	 * @param type
	 * @return
	 */
	public static FixedTypeDecoder isFinalDecoder(Class type) {
		if (type == String.class) {
			return StringDecoder.instance;
		}
		if (type.isEnum()) {
			return new EnumDecoder(type);
		}

		if (type == boolean.class || type == Boolean.class) {
			return BooleanDecoder.instance;
		}
		if (type == byte.class || type == Byte.class) {
			return ByteDecoder.instance;
		}
		if (type == char.class || type == Character.class) {
			return CharDecoder.instance;
		}
		if (type == Class.class) {
			return ClazzDecoder.instance;
		}

		// ====number decoder
		if (type == double.class || type == Double.class) {
			return DoubleDecoder.instance;
		}
		if (type == float.class || type == Float.class) {
			return FloatDecoder.instance;
		}
		if (type == int.class || type == Integer.class) {
			return IntegerDecoder.instance;
		}
		if (type == long.class || type == Long.class) {
			return LongDecoder.instance;
		}
		if (type == short.class || type == Short.class) {
			return ShortDecoder.instance;
		}

		// simple object
		if (type == Locale.class) {
			return LocaleDecoder.instance;
		}
		if (type == Pattern.class) {
			return PatternDecoder.instance;
		}
		if (type == URL.class) {
			return URLDecoder.instance;
		}
		if (type == URI.class) {
			return URIDecoder.instance;
		}
		if (type == UUID.class) {
			return UUIDDecoder.instance;
		}
		// not final type
		if (TimeZone.class.isAssignableFrom(type)) {
			return TimeZoneDecoder.instance;
		}
		if (InetAddress.class.isAssignableFrom(type)) {
			return InetAddressDecoder.instance;
		}
		if (BigDecimal.class.isAssignableFrom(type)) {
			return BigDecimalDecoder.instance;
		}
		if (BigInteger.class.isAssignableFrom(type)) {
			return BigIntegerDecoder.instance;
		}
		if (Charset.class.isAssignableFrom(type)) {
			return CharsetDecoder.instance;
		}
		if (SocketAddress.class.isAssignableFrom(type)) {
			return SocketAddressDecoder.instance;
		}

		return null;
	}

	public static SpecTypeDecoder isSpecTypeDecoder(Class type) {
		if (Appendable.class.isAssignableFrom(type)) {
			return new AppendableDecoder(type);
		}
		if (Date.class.isAssignableFrom(type)) {
			return new DateDecoder(type);
		}
		if (File.class.isAssignableFrom(type)) {
			return new FileDecoder(type);
		}
		if (AtomicBoolean.class.isAssignableFrom(type)) {
			return new AtomicBooleanDecoder(type);
		}
		if (AtomicInteger.class.isAssignableFrom(type)) {
			return new AtomicIntegerDecoder(type);
		}
		if (AtomicIntegerArray.class.isAssignableFrom(type)) {
			return new AtomicIntegerArrayDecoder(type);
		}
		if (AtomicLong.class.isAssignableFrom(type)) {
			return new AtomicLongDecoder(type);
		}
		if (AtomicLongArray.class.isAssignableFrom(type)) {
			return new AtomicLongArrayDecoder(type);
		}
		if (AtomicReference.class.isAssignableFrom(type)) {
			return new AtomicReferenceDecoder(type);
		}
		if (AtomicReferenceArray.class.isAssignableFrom(type)) {
			return new AtomicReferenceArrayDecoder(type);
		}
		if (SimpleDateFormat.class.isAssignableFrom(type)) {
			return new SimpleDateFormatDecoder(type);
		}
		return null;
	}
}
