package org.jtester.testng;

import java.lang.reflect.Method;
import java.util.List;

import org.jtester.core.JTesterHelper;
import org.jtester.core.ListenerExecutor;
import org.jtester.core.testng.JTesterHookable;
import org.jtester.exception.ExceptionWrapper;
import org.jtester.fit.ErrorRecorder;
import org.jtester.module.core.CoreModule;
import org.jtester.utility.ArrayHelper;
import org.jtester.utility.ListHelper;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("rawtypes")
@Test(groups = "all-test")
public abstract class JTester extends JTesterHookable {

	static {
		CoreModule.initSingletonInstance();

		ErrorRecorder.createNewErrorFile();
	}

	@BeforeClass(alwaysRun = true)
	public void aBeforeClass(ITestContext context) {
		JTesterHelper.invokeSpringInitMethod(this);
		this.dealJMockitTestDecorator(context);
		this.error_setup_class = ListenerExecutor.executeSetupClass(this.getClass());
	}

	@BeforeMethod(alwaysRun = true)
	public void aBeforeMethod(Method testedMethod) {
		this.error_setup_method = ListenerExecutor.executeSetupMethod(this, testedMethod);
	}

	@AfterMethod(alwaysRun = true)
	public void zAfterMethod(Method testedMethod) {
		Throwable throwable = ListenerExecutor.executeTeardownMethod(this, testedMethod);
		ExceptionWrapper.throwRuntimeException(throwable);
	}

	@AfterClass(alwaysRun = true)
	public void zAfterClass() {
		Throwable throwable = ListenerExecutor.executeTeardownClass(this);
		ExceptionWrapper.throwRuntimeException(throwable);
	}

	/**
	 * 构造对象数组
	 * 
	 * @param objs
	 * @return
	 */
	public Object[] toArray(Object... objs) {
		Object[] arr = ArrayHelper.toArray(objs);
		return arr;
	}

	/**
	 * 构造list列表
	 * 
	 * @param objs
	 * @return
	 */
	public List toList(Object... objs) {
		List list = ListHelper.toList(objs);
		return list;
	}
}
