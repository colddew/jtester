package org.jtester.module.tracer.spring;

import java.util.Stack;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jtester.module.tracer.TracerManager;

@SuppressWarnings("rawtypes")
public class SpringBeanTracer implements MethodInterceptor {// MethodInterceptor

	public Object invoke(MethodInvocation invocation) throws Throwable {
		final Stack<Class> beans = BeanTracerManager.monitorBeans();
		if (beans == null) {
			return invocation.proceed();
		}
		Class beanClazz = invocation.getThis().getClass();
		String method = invocation.getMethod().getName();
		Object[] paras = invocation.getArguments();
		MethodTracerEvent tracerEvent = addTraceEvent(beans, beanClazz, method, paras);
		TracerManager.addBeanTracerEvent(tracerEvent);
		try {
			Object result = invocation.proceed();
			TracerManager.addBeanTracerResult(tracerEvent, result);
			return result;
		} catch (Throwable e) {
			TracerManager.addBeanTracerException(tracerEvent, e);
			throw e;
		} finally {
			beans.pop();
		}
	}

	/**
	 * 往beans stacks中增加spring监视
	 * 
	 * @param beans
	 * @param beanClaz
	 * @param methodName
	 */
	private MethodTracerEvent addTraceEvent(Stack<Class> beans, Class beanClaz, String methodName, Object[] args) {
		final MethodTracerEvent tracerEvent = new MethodTracerEvent(beans.peek(), beanClaz, methodName);
		beans.push(beanClaz);
		for (Object para : args) {
			tracerEvent.addPara(para);
		}

		return tracerEvent;
	}
}
