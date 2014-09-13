package org.jtester.module.core;

import java.lang.reflect.Method;

import org.jtester.core.TestedObject;
import org.jtester.module.TestListener;
import org.jtester.module.core.helper.ConfigurationHelper;
import org.jtester.module.core.helper.SpringModuleHelper;
import org.jtester.module.spring.ApplicationContextFactory;
import org.jtester.module.spring.JTesterBeanFactory;
import org.jtester.module.spring.strategy.injector.SpringBeanInjector;

@SuppressWarnings("rawtypes")
public class SpringModule implements Module {
	private ApplicationContextFactory contextFactory;

	/**
	 * 根据配置初始化ApplicationContextFactory <br>
	 * <br> {@inheritDoc}
	 */
	public void init() {
		contextFactory = ConfigurationHelper.getInstance(SPRING_APPLICATION_CONTEXT_FACTORY_CLASS_NAME);
	}

	public void afterInit() {
	}

	/**
	 * 强制让springapplicationcontext失效，重新初始化
	 * 
	 * @param testedObject
	 */
	public void invalidateApplicationContext() {
		Class testClazz = TestedObject.currTestedClazz();
		TestedObject.removeSpringContext();
		JTesterBeanFactory beanFactory = SpringModuleHelper.initSpringContext(testClazz, this.contextFactory);
		TestedObject.setSpringContext(beanFactory);
	}

	public TestListener getTestListener() {
		return new SpringTestListener();
	}

	/**
	 * The {@link TestListener} for this module
	 */
	protected class SpringTestListener extends TestListener {
		@Override
		public void setupClass(Class testClazz) {
			JTesterBeanFactory beanFactory = SpringModuleHelper.initSpringContext(testClazz, contextFactory);
			TestedObject.setSpringContext(beanFactory);
		}

		/**
		 * 重新注入spring bean,避免字段的值受上个测试的影响<br>
		 * <br> {@inheritDoc}
		 */
		@Override
		public void setupMethod(Object testObject, Method testMethod) {
			JTesterBeanFactory beanFactory = (JTesterBeanFactory) TestedObject.getSpringBeanFactory();
			if (beanFactory != null) {
				SpringBeanInjector.injectSpringBeans(beanFactory, testObject);
			}
		}

		@Override
		public void teardownClass(Object testedObject) {
			TestedObject.removeSpringContext();
		}

		@Override
		protected String getName() {
			return "SpringTestListener";
		}
	}
}
