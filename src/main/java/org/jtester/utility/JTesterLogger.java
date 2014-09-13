package org.jtester.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import static org.jtester.module.core.ConfigurationConst.LOG4J_XML_FILE;

import org.jtester.bytecode.reflector.helper.ClazzHelper;
import org.jtester.bytecode.reflector.helper.MethodHelper;
import org.jtester.module.core.helper.ConfigurationHelper;

public class JTesterLogger {
	public static final int DEBUG = 0;

	public static final int INFO = 1;

	public static final int WARNING = 2;

	public static final int ERROR = 3;

	public static int level = DEBUG;

	private static void mark(String marker) {
		System.out.println(marker);
	}

	public static void debug(String info) {
		if (level <= DEBUG) {
			mark("DEBUG: " + info);
		}
	}

	public static void warn(String warn) {
		if (level <= WARNING) {
			mark("WARNING: " + warn);
		}
	}

	public static void warn(String warn, Throwable e) {
		if (level <= WARNING) {
			mark("WARNING: " + warn);
			e.printStackTrace();
		}
	}

	public static void info(String info) {
		if (level <= INFO) {
			mark("INFO: " + info);
		}
	}

	public static void error(String err, Throwable e) {
		mark("ERROR: " + err);
		e.printStackTrace();
	}

	public static void mark(String marker, Throwable e) {
		System.out.println(marker);
		e.printStackTrace();
	}

	/**
	 * 重置log4j的设置
	 */
	@SuppressWarnings("rawtypes")
	public static void resetLog4jLevel() {
		String log4jxml = ConfigurationHelper.getString(LOG4J_XML_FILE);

		boolean log4jAvailable = ClazzHelper.isClassAvailable("org.apache.log4j.xml.DOMConfigurator");
		if (StringHelper.isBlankOrNull(log4jxml) || log4jAvailable == false) {
			return;
		}
		try {
			URL url = ResourceHelper.getResourceUrl(log4jxml);
			Class domConfigurator = ClazzHelper.getClazz("org.apache.log4j.xml.DOMConfigurator");
			MethodHelper.invokeStatic(domConfigurator, "configure", url);
		} catch (Throwable e) {
			mark("reset log4j leve error, " + e == null ? "null" : e.getMessage());
		}
	}

	private static File debugFile = new File(System.getProperty("user.dir") + "/target/jtester.log");

	/**
	 * 用于记录jtester运行时的信息，方便定位复杂的问题<br>
	 * 正式发布版本中所有方法都改为protected，禁止其它类引用
	 * 
	 * @author darui.wudr
	 * 
	 */
	protected static void writerDebugInfo(String info) {
		Writer writer = null;
		try {
			writer = new FileWriter(debugFile, true);
			writer.write(info);
			writer.write("\n");
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
