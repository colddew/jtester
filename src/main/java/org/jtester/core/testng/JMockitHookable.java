package org.jtester.core.testng;

import java.io.File;
import java.util.regex.Pattern;

import org.jtester.bytecode.reflector.FieldAccessor;
import org.jtester.bytecode.reflector.MethodAccessor;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestRunner;
import org.testng.internal.IConfiguration;

public class JMockitHookable {
	private final static MethodAccessor<Void> runMethod = new MethodAccessor<Void>(IHookable.class, "run",
			IHookCallBack.class, ITestResult.class);

	private final static FieldAccessor<IConfiguration> mField = new FieldAccessor<IConfiguration>(TestRunner.class,
			"m_configuration");

	/**
	 * jmockit的hookable<br>
	 * 
	 * @see mockit.integration.testng.internal.TestNGRunnerDecorator
	 */
	private final IHookable hookable;

	public JMockitHookable(final ITestContext context) {
		if (context == null) {
			throw new RuntimeException("the testng conext can't be null.");
		}
		IConfiguration m_configuration = mField.get(context);
		if (m_configuration == null) {
			this.hookable = null;
		} else {
			this.hookable = m_configuration.getHookable();
			if (this.hookable == null) {
				String jarFilePath = getJMockitJarFilePath();
				String hits = "JMockit has not been initialized. Check that your Java VM has been started with the -javaagent:"
						+ jarFilePath + " command line option.";
				throw new RuntimeException(hits);
			}
		}
	}

	public void run(IHookCallBack callBack, ITestResult testResult) {
		if (this.hookable == null) {
			callBack.runTestMethod(testResult);
			return;
		} else {
			runMethod.invokeUnThrow(this.hookable, new Object[] { callBack, testResult });
		}
	}

	private static final Pattern JAR_REGEX = Pattern.compile(".*jmockit[-.\\d]*.jar");

	private static String getJMockitJarFilePath() {
		String javaClazzPaths = System.getProperty("java.class.path");
		if (javaClazzPaths == null) {
			return "jmockit.jar";
		}
		String[] classPath = javaClazzPaths.split(File.pathSeparator);
		if (classPath == null) {
			return "jmockit.jar";
		}
		for (String cpEntry : classPath) {
			if (JAR_REGEX.matcher(cpEntry).matches()) {
				return cpEntry;
			}
		}

		return "jmockit.jar";
	}
}
