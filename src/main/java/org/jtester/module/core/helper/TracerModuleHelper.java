package org.jtester.module.core.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;

import org.jtester.utility.ResourceHelper;
import org.jtester.utility.StringHelper;

public class TracerModuleHelper {

	public final static String TRACE_JDBC_KEY = "tracer.database";
	public final static String TRACE_SPRING_KEY = "tracer.springbean";

	// /**
	// * 创建负责跟踪jdbc信息的代理datasource
	// *
	// * @param dataSource
	// * @return
	// */
	// public static DataSource tracerDataSource(DataSource dataSource) {
	// if (traceJDBC()) {
	// return new JTesterDataSource(dataSource);
	// } else {
	// return dataSource;
	// }
	// }

	public static boolean traceJDBC() {
		boolean traceJDBC = ConfigurationHelper.getBoolean(TRACE_JDBC_KEY);
		return traceJDBC;
	}

	public static boolean traceSpringBean() {
		boolean traceBean = ConfigurationHelper.getBoolean(TRACE_SPRING_KEY);
		return traceBean;
	}

	/**
	 * 返回由classpath + classname#methodname+ surfix组成的文件
	 * 
	 * @param testObject
	 * @param testMethod
	 * @param surfix
	 * @return
	 */
	public static File getAboutMethodFile(Object testObject, Method testMethod, String surfix) {
		String basedir = System.getProperty("user.dir") + "/target/tracer/";
		String clazzName = testObject.getClass().getName();
		String methodName = testMethod.getName();
		String path = basedir + clazzName.replace('.', '/');
		File htmlFile = new File(path + "#" + methodName + surfix);
		ResourceHelper.mkFileParentDir(htmlFile);
		return htmlFile;
	}

	/**
	 * 输出spring bean和jdbc跟踪信息
	 * 
	 * @param htmlFile
	 * @param beanTracerInfo
	 * @param jdbcTracerInfo
	 */
	public static void writeTracerInfo(File htmlFile, String jpgFile, String beanTracerInfo, String jdbcTracerInfo) {
		try {
			FileWriter writer = new FileWriter(htmlFile);
			writer.append("<html><head>");
			writer.append(String.format("<META HTTP-EQUIV='Content-Type' CONTENT='text/html; charset=%s'>",
					ResourceHelper.defaultFileEncoding()));
			writer.append("<style>");
			InputStream is = ResourceHelper.getResourceAsStream("org/jtester/testng/UserTestReporter.css");

			if (is != null) {
				String style = ResourceHelper.readFromStream(is);
				writer.append(style);
			}
			writer.append("</style></head>");
			if (jpgFile != null) {
				writer.append(String.format("<img src='%s'/><br/>", URLEncoder.encode(jpgFile, "utf-8")));
			}

			if (StringHelper.isBlankOrNull(beanTracerInfo) == false) {
				writer.write(beanTracerInfo);
			}
			writer.write("<br/>");
			if (StringHelper.isBlankOrNull(jdbcTracerInfo) == false) {
				writer.write(jdbcTracerInfo);
			}
			writer.append("</html>");

			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
