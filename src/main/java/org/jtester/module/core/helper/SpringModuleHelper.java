package org.jtester.module.core.helper;

import java.util.Arrays;

import org.jtester.annotations.SpringApplicationContext;
import org.jtester.core.TestedObject;
import org.jtester.exception.JTesterException;
import org.jtester.module.core.SpringModule;
import org.jtester.module.spring.ApplicationContextFactory;
import org.jtester.module.spring.JTesterBeanFactory;
import org.jtester.module.spring.JTesterSpringContext;
import org.jtester.utility.AnnotationUtils;
import org.jtester.utility.JTesterLogger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

public class SpringModuleHelper {

	/**
	 * 获得当前测试类spring容器中名称为beanname的spring bean
	 * 
	 * @param beanName
	 * @return
	 */
	public static Object getBeanByName(String beanname) {
		BeanFactory factory = (BeanFactory) TestedObject.getSpringBeanFactory();
		if (factory == null) {
			throw new RuntimeException("can't find SpringApplicationContext for tested class:"
					+ TestedObject.currTestedClazzName());
		} else {
			Object bean = factory.getBean(beanname);
			return bean;
		}
	}

	/**
	 * 强制重新加载spring 容器<br>
	 * Forces the reloading of the application context the next time that it is
	 * requested. If classes are given only contexts that are linked to those
	 * classes will be reset. If no classes are given, all cached contexts will
	 * be reset.
	 * 
	 * @param classes
	 *            The classes for which to reset the contexts
	 */
	public static void invalidateApplicationContext() {
		boolean springModuleEnabled = ModulesManager.isModuleEnabled(SpringModule.class);
		if (springModuleEnabled) {
			SpringModule module = ModulesManager.getModuleInstance(SpringModule.class);
			module.invalidateApplicationContext();
		}
	}

	/**
	 * 初始化当前测试类用到的spring application context对象
	 * 
	 * @param testedObject
	 * @param contextFactory
	 * @return does initial spring context successfully
	 */
	@SuppressWarnings("rawtypes")
	public static JTesterBeanFactory initSpringContext(Class testClazz, ApplicationContextFactory contextFactory) {
		JTesterBeanFactory beanFactory = (JTesterBeanFactory) TestedObject.getSpringBeanFactory();
		if (beanFactory != null) {
			return beanFactory;
		}
		SpringApplicationContext annotation = AnnotationUtils.getClassLevelAnnotation(SpringApplicationContext.class,
				testClazz);
		if (annotation == null) {
			return null;
		}

		long startTime = System.currentTimeMillis();

		String[] locations = annotation.value();
		boolean ignoreNoSuchBean = annotation.ignoreNoSuchBean();
		JTesterSpringContext context = contextFactory.createApplicationContext(Arrays.asList(locations),
				ignoreNoSuchBean);

		context.refresh();
		long duration = System.currentTimeMillis() - startTime;
		JTesterLogger.warn(String.format("take %d ms to init spring context for test obejct[%s]", duration,
				testClazz.getName()));

		beanFactory = context.getJTesterBeanFactory();
		TestedObject.setSpringContext(beanFactory);
		return beanFactory;
	}

	/**
	 * 释放测试类的spring容器
	 * 
	 * @param springContext
	 *            AbstractApplicationContext实例，这里定义为Object是方便其它模块脱离spring依赖
	 */
	public static void closeSpringContext(Object beanFactory) {
		if (beanFactory == null) {
			return;
		}
		if (beanFactory instanceof JTesterBeanFactory) {
			((JTesterBeanFactory) beanFactory).destroySingletons();
			JTesterLogger.warn("close spring context for class:" + TestedObject.currTestedClazzName());
		} else {
			String error = String.format("there must be something error, the type[%s] object isn't a spring context.",
					beanFactory.getClass().getName());
			throw new RuntimeException(error);
		}
	}

	/**
	 * 增加自动跟踪的auto tracer bean definition
	 * 
	 * @param beanFactory
	 */
	public static void addTracerBeanDefinition(final BeanDefinitionRegistry beanFactory) {
		AbstractBeanDefinition pointcut = new GenericBeanDefinition();
		pointcut.setBeanClassName(org.jtester.module.tracer.spring.TracerMethodRegexPointcut.class.getName());
		pointcut.setScope("singleton");
		pointcut.setAutowireCandidate(false);

		pointcut.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);

		beanFactory.registerBeanDefinition("jtester-internal-methodname-pointcut", pointcut);

		AbstractBeanDefinition advice = new GenericBeanDefinition();
		advice.setBeanClassName(org.jtester.module.tracer.spring.SpringBeanTracer.class.getName());
		advice.setScope("singleton");
		advice.setAutowireCandidate(false);

		advice.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);

		beanFactory.registerBeanDefinition("jtester-internal-springbeantracer", advice);

		AbstractBeanDefinition advisor = new GenericBeanDefinition();
		advisor.setBeanClassName(org.springframework.aop.support.DefaultPointcutAdvisor.class.getName());
		advisor.setScope("singleton");
		advisor.setAutowireCandidate(false);

		advisor.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
		advisor.getPropertyValues().addPropertyValue("pointcut",
				new RuntimeBeanReference("jtester-internal-methodname-pointcut"));
		advisor.getPropertyValues().addPropertyValue("advice",
				new RuntimeBeanReference("jtester-internal-springbeantracer"));

		beanFactory.registerBeanDefinition("jtester-internal-beantracer-advisor", advisor);
	}

	/**
	 * 返回spring代理的目标对象
	 * 
	 * @param target
	 * @return
	 */
	public static Object getAdvisedObject(Object target) {
		if (target instanceof org.springframework.aop.framework.Advised) {
			try {
				return ((org.springframework.aop.framework.Advised) target).getTargetSource().getTarget();
			} catch (Exception e) {
				throw new JTesterException(e);
			}
		} else {
			return target;
		}
	}
}
