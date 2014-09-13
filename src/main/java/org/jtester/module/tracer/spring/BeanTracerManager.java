package org.jtester.module.tracer.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.jtester.annotations.Tracer;
import org.jtester.annotations.Tracer.Info;
import org.jtester.module.tracer.TracerSequece;

@SuppressWarnings({ "rawtypes" })
public class BeanTracerManager {
	private static ThreadLocal<List<MethodTracerEvent>> threadTracerEvents = new ThreadLocal<List<MethodTracerEvent>>();

	private static ThreadLocal<Stack<Class>> monitorBeans = new ThreadLocal<Stack<Class>>();

	private static ThreadLocal<Info> stringTypes = new ThreadLocal<Info>();

	private static ThreadLocal<Boolean> isBeanEnabled = new ThreadLocal<Boolean>();

	public static boolean isTracerEnabled() {
		Boolean bl = isBeanEnabled.get();
		return bl == null ? false : bl;
	}

	/**
	 * 启用当前线程的spring bean跟踪器<br>
	 * 重新初始化beans监控堆栈，压入测试类class
	 * 
	 * @param testMethodName
	 */
	public static void initMonitorBeans(boolean isTracer, Class testClazz, Tracer tracer) {
		isBeanEnabled.set(isTracer);
		Stack<Class> beans = monitorBeans();
		List<MethodTracerEvent> tracers = threadTracerEvents.get();

		if (beans == null) {
			beans = new Stack<Class>();
			monitorBeans.set(beans);
		}

		if (tracers == null) {
			tracers = new ArrayList<MethodTracerEvent>();
			threadTracerEvents.set(tracers);
		}

		beans.clear();
		beans.add(testClazz);
		tracers.clear();

		Info type = tracer == null ? Info.TOJSON : tracer.info();
		stringTypes.set(type);
	}

	/**
	 * 往当前spring bean跟踪记录器中增加一条跟踪信息
	 * 
	 * @param tracer
	 */
	public static void addTracer(MethodTracerEvent tracer) {
		tracer.clean();
		List<MethodTracerEvent> tracers = threadTracerEvents.get();
		tracers.add(tracer);
	}

	/**
	 * 结束当前线程的spring bean跟踪器<br>
	 * 返回序列图描述串
	 */
	public static String endMonitorBean() {
		Stack<Class> stack = monitorBeans.get();
		if (stack != null) {
			stack.clear();
		}
		monitorBeans.remove();
		List<MethodTracerEvent> events = threadTracerEvents.get();
		if (events == null || events.size() == 0) {
			threadTracerEvents.remove();
			return null;
		}

		String sequence = TracerSequece.getSequenceDescription(events);

		if (events != null) {
			events.clear();
		}
		threadTracerEvents.remove();

		return sequence;
	}

	/**
	 * 判断当前线程中的spring bean是否处于被监控状态
	 * 
	 * @return
	 */
	public static Stack<Class> monitorBeans() {
		return monitorBeans.get();
	}

	/**
	 * 返回线程中当前活动spring bean
	 * 
	 * @return
	 */
	public static Class getLastBeanClazz() {
		Stack<Class> beans = monitorBeans();
		if (beans == null || beans.size() == 1) {
			return null;
		} else {
			return beans.peek();
		}
	}

	/**
	 * 序列化对象
	 * 
	 * @param o
	 * @return
	 */
	public static String toTracerString(Object o) {
		Info type = stringTypes.get();
		String info = type.toInfoString(o);
		return info;
	}
}
