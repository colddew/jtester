package org.jtester.module.tracer;

import java.util.HashMap;
import java.util.Map;

import org.jtester.utility.StringHelper;

@SuppressWarnings({ "rawtypes" })
public class TracerBeanManager {
	private static Map<Class, String> beanNameMap = new HashMap<Class, String>();

	/**
	 * 记录spring bean的名称
	 * 
	 * @param bean
	 * @param beanName
	 */
	public static void registerBean(Object bean, String beanName) {
		if (bean == null) {
			return;
		}
		beanNameMap.put(bean.getClass(), beanName);
	}

	/**
	 * 返回clazz相对应的bean name
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getBeanName(Class clazz) {
		if (clazz == null) {
			return "<null>";
		}
		String beanName = beanNameMap.get(clazz);
		if (beanName == null) {
			return clazz.getSimpleName();
		} else {
			return beanName;
		}
	}

	/**
	 * 清空已注册的spring bean name信息，重新注册
	 */
	public static void clear() {
		beanNameMap.clear();
	}

	public static String getSourceBeanName(final Class source) {
		String beanName = TracerBeanManager.getBeanName(source);
		return StringHelper.isBlankOrNull(beanName) ? "test-class" : beanName;
	}

	public static String getTargetBeanName(final Class target) {
		return TracerBeanManager.getBeanName(target);
	}
}
