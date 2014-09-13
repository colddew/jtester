package org.jtester.module.tracer.jdbc;

import java.util.LinkedList;
import java.util.Queue;

import org.jtester.module.tracer.spring.BeanTracerManager;

/**
 * Jdbc跟踪器管理
 */
@SuppressWarnings({ "rawtypes" })
public class JdbcTracerManager {
	public static ThreadLocal<Queue<JdbcTracerEvent>> threadTracerEvents = new ThreadLocal<Queue<JdbcTracerEvent>>();
	private static ThreadLocal<Boolean> isJdbcEnabled = new ThreadLocal<Boolean>();

	public static void initJdbcTracer(boolean isTracer) {
		Queue<JdbcTracerEvent> tracerEvents = threadTracerEvents.get();
		if (tracerEvents == null) {
			tracerEvents = new LinkedList<JdbcTracerEvent>();
			threadTracerEvents.set(tracerEvents);
		}
		tracerEvents.clear();
		isJdbcEnabled.set(isTracer);
	}

	/**
	 * 当前线程是否处于监控状态
	 * 
	 * @return
	 */
	public static boolean isTracerEnabled() {
		Boolean bl = isJdbcEnabled.get();
		return bl == null ? false : bl;
	}

	public static Queue<JdbcTracerEvent> disableJdbcTracer() {
		Queue<JdbcTracerEvent> tracerEvents = threadTracerEvents.get();
		threadTracerEvents.remove();
		isJdbcEnabled.set(false);
		return tracerEvents;
	}

	public static void addSQLTracer(String sql) {
		Queue<JdbcTracerEvent> tracerEvents = threadTracerEvents.get();
		boolean isTracer = isJdbcEnabled.get();
		if (tracerEvents == null || isTracer == false) {
			return;
		}
		Class beanClazz = BeanTracerManager.getLastBeanClazz();
		JdbcTracerEvent tracerEvent = new JdbcTracerEvent(beanClazz, sql);
		tracerEvents.add(tracerEvent);
	}

	/**
	 * 当前线程继续监控jdbc活动
	 */
	public static void continueThreadMonitorJdbc() {
		isJdbcEnabled.set(true);
	}

	/**
	 * 当前线程暂停监控jdbc活动
	 */
	public static void suspendThreadMonitorJdbc() {
		isJdbcEnabled.set(false);
	}

	/**
	 * 结束sql tracer监控，返回sql监控列表(html)
	 * 
	 * @return
	 */
	public static String endTracer() {
		Queue<JdbcTracerEvent> tracerEvents = threadTracerEvents.get();
		boolean isTrace = isJdbcEnabled.get();
		if (tracerEvents == null || isTrace == false) {
			return null;
		}
		StringBuffer buffer = new StringBuffer("<table>");
		buffer.append("<tr><td>spring-bean</td><td>SQL-Statement</td></tr>");

		for (JdbcTracerEvent tracer : tracerEvents) {
			buffer.append(tracer.toHtmlString());
		}
		buffer.append("</table>");

		tracerEvents.clear();
		threadTracerEvents.remove();

		return buffer.toString();
	}
}
