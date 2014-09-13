package org.jtester.module.core;

import java.lang.reflect.Method;

import org.jtester.core.Startup;
import org.jtester.module.TestListener;
import org.jtester.utility.JTesterLogger;

public class JMockitModule implements Module {

	public void init() {
		JTesterLogger.info("init jmockit in jmockit module.");

		// mockit.internal.startup.Startup.initializeIfNeeded();
		Startup.initializeIfNeeded();
	}

	public void afterInit() {
	}

	public TestListener getTestListener() {
		return new JMockitTestListener();
	}

	protected class JMockitTestListener extends TestListener {
		/**
		 * {@inheritDoc}<br>
		 * jmockit-0.999.9 bug fixed:<br>
		 * 测试类在 mockit.integration.testng.internal.TestNGRunnerDecorator 方法<br>
		 * run(IHookCallBack callBack, ITestResult testResult) -><br>
		 * executeTestMethod(IHookCallBack callBack, ITestResult testResult) -><br>
		 * cleanUpAfterTestMethodExecution() -><br>
		 * ExecutingTest..void finishExecution()中将全局的 nonStrictMocks
		 * 列表清空，导致后续的Expectations()出错
		 */
		public void setupMethod(Object testObject, Method testMethod) {
			// JMockitHookable.reRegistedMockField(testObject);
		}

		@Override
		public void teardownClass(Object testObject) {
			// Mockit.tearDownMocks();
		}

		@Override
		protected String getName() {
			return "JMockitTestListener";
		}
	}
}
