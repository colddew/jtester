package org.jtester.module.tracer;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;

import org.jtester.annotations.Tracer;
import org.jtester.module.core.helper.TracerModuleHelper;
import org.jtester.module.tracer.jdbc.JdbcTracerManager;
import org.jtester.module.tracer.spring.BeanTracerManager;
import org.jtester.module.tracer.spring.MethodTracerEvent;
import org.jtester.utility.ResourceHelper;
import org.jtester.utility.StringHelper;

@SuppressWarnings({ "rawtypes" })
public class TracerManager {
	static ThreadLocal<File> tracerFile = new ThreadLocal<File>();

	/**
	 * 设置是否跟踪spring或jdbc跟踪信息
	 * 
	 * @param tracer
	 * @param testedClazz
	 */
	public static void startTracer(Tracer tracer, Class testedClazz, Method testMethod) {
		boolean traceBean = false;
		boolean traceJdbc = false;
		if (tracer != null) {
			traceBean = tracer.spring();
			traceJdbc = tracer.jdbc();
		} else {
			traceBean = TracerModuleHelper.traceSpringBean();
			traceJdbc = TracerModuleHelper.traceJDBC();
		}

		if (traceBean) {
			BeanTracerManager.initMonitorBeans(traceBean, testedClazz, tracer);
		}
		if (traceJdbc) {
			JdbcTracerManager.initJdbcTracer(traceJdbc);
		}

		File htmlFile = getAboutMethodFile(testedClazz, testMethod, ".html");

		tracerFile.set(null);
		if (traceBean || traceJdbc) {
			tracerFile.set(htmlFile);
			writeTraceHtmlFile(htmlFile);
		} else {
			tracerFile.set(null);
		}
	}

	private static void writeTraceHtmlFile(File htmlFile) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(htmlFile, false);
			writer.append("<html><head>");
			writer.append(String.format("<META HTTP-EQUIV='Content-Type' CONTENT='text/html; charset=%s'>",
					ResourceHelper.defaultFileEncoding()));
			writer.append("<style>");
			String css = ResourceHelper.readFromFile("org/jtester/testng/UserTestReporter.css");
			writer.append(css);
			writer.append("</style></head>");
			writer.append("<table>");
		} catch (Exception e) {
			stopExceptionThrow(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Throwable e) {
					stopExceptionThrow(e);
				}
			}
		}
	}

	/**
	 * 往当前spring bean跟踪记录器中增加一条跟踪信息
	 * 
	 * @param tracer
	 */
	public static void addBeanTracerEvent(MethodTracerEvent event) {
		if (event == null) {
			return;
		}
		String info = event.toHtmlString();
		appendHtmlFile(info);
		BeanTracerManager.addTracer(event);
	}

	/**
	 * 记录bean跟踪api返回值
	 * 
	 * @param result
	 */
	public static void addBeanTracerResult(final MethodTracerEvent event, final Object result) {
		if (event == null) {
			return;
		}
		String info = BeanTracerManager.toTracerString(result);
		String html = MethodTracerEvent.getResultHtml(event.getSourceClazz(), event.getTargetClazz(),
				event.getMethodName(), info);
		appendHtmlFile(html);
	}

	public static void addBeanTracerException(final MethodTracerEvent event, final Throwable e) {
		if (event == null) {
			return;
		}
		String info = "throw exception:\n" + StringHelper.exceptionTrace(e);
		String html = MethodTracerEvent.getResultHtml(event.getSourceClazz(), event.getTargetClazz(),
				event.getMethodName(), info);
		appendHtmlFile(html);
	}

	private static void appendHtmlFile(String html) {
		File htmlFile = tracerFile.get();
		boolean traceBean = BeanTracerManager.isTracerEnabled();
		if (htmlFile == null || traceBean == false) {
			return;
		}
		try {
			FileWriter writer = new FileWriter(htmlFile, true);
			writer.append(html);
			writer.close();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static void addJdbcTracerEvent(String sql) {
		boolean traceJdbc = JdbcTracerManager.isTracerEnabled();

		if (traceJdbc == false) {
			return;
		}
		JdbcTracerManager.addSQLTracer(sql);
	}

	/**
	 * 结束输出spring bean和jdbc跟踪信息
	 * 
	 * @param htmlFile
	 * @param beanTracerInfo
	 * @param jdbcTracerInfo
	 */
	public static void endTracer(Class testClazz, String method) {
		File htmlFile = tracerFile.get();
		boolean traceBean = BeanTracerManager.isTracerEnabled();
		if (htmlFile == null || traceBean == false) {
			return;
		}

		try {
			String sequenceDescription = BeanTracerManager.endMonitorBean();
			String sql = JdbcTracerManager.endTracer();

			writeSequenceChart(htmlFile, sequenceDescription, sql);
		} catch (Throwable e) {
			String message = String.format("test class:%s, test method:%s.", testClazz.getName(), method);
			Exception newE = new RuntimeException(message, e);
			// 这里无法做任何处理，我也不想把异常再往外面抛
			stopExceptionThrow(newE);
		}
	}

	/**
	 * 停止异常的传播，打印异常堆栈
	 * 
	 * @param e
	 */
	private static void stopExceptionThrow(Throwable e) {
		if (e != null) {
			e.printStackTrace();
		}
	}

	/**
	 * 记录spring bean的序列图描述语句
	 * 
	 * @param htmlFile
	 * @param jpgFile
	 */
	private static void writeSequenceChart(File htmlFile, String sequenceDescription, String sql) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(htmlFile, true);

			writer.write("</table><br/>");

			if (StringHelper.isBlankOrNull(sequenceDescription) == false) {
				writer.append("<br/><div>").append(sequenceDescription).append("</div><br/>");
			}

			if (StringHelper.isBlankOrNull(sql) == false) {
				writer.write(sql);
			}
			writer.append("</html>");

		} catch (Exception e) {
			stopExceptionThrow(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Throwable e) {
					stopExceptionThrow(e);
				}
			}
		}
	}

	/**
	 * 返回由classpath + classname#methodname+ surfix组成的文件
	 * 
	 * @param testObject
	 * @param testMethod
	 * @param surfix
	 * @return
	 */
	private static File getAboutMethodFile(Class testClazz, Method testMethod, String surfix) {
		String basedir = System.getProperty("user.dir") + "/target/tracer/";
		String clazzName = testClazz.getName();
		String methodName = testMethod.getName();
		String path = basedir + clazzName.replace('.', '/');
		File htmlFile = new File(path + "#" + methodName + surfix);
		ResourceHelper.mkFileParentDir(htmlFile);
		return htmlFile;
	}
}
