package org.jtester.core;

import java.lang.instrument.Instrumentation;

public final class Startup {
	static {
		try {
			Class.forName("org.jtester.hamcrest.TheStyleAssertion");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("preload class error.");
		}
	}

	public static void premain(String agentArgs, Instrumentation inst) throws Exception {
		mockit.internal.startup.Startup.premain(agentArgs, inst);
	}

	public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
		mockit.internal.startup.Startup.agentmain(agentArgs, inst);
	}

	public static Instrumentation instrumentation() {
		return mockit.internal.startup.Startup.instrumentation();
	}

	public static void initializeIfNeeded() {
		mockit.internal.startup.Startup.initializeIfNeeded();

	}

//	private static void initialize(String agentArgs, Instrumentation inst) throws IOException, ClassNotFoundException {
//		instrumentation = inst;
//
//		// Pre-load the mocking bridge to avoid a ClassCircularityError.
//		Class.forName("mockit.internal.MockingBridge");
//
//		loadInternalStartupMocks();
//
//		if (agentArgs != null && agentArgs.length() > 0) {
//			processAgentArgs(agentArgs);
//		}
//
//		for (String toolSpec : defaultTools) {
//			loadExternalTool(toolSpec, true);
//		}
//
//		instrumentation.addTransformer(new ProxyRegistrationTransformer());
//		instrumentation.addTransformer(new ExpectationsTransformer(inst));
//	}

	public static void verifyInitialization() {
		mockit.internal.startup.Startup.verifyInitialization();
	}

	public static boolean isJava6OrLater() {
		return mockit.internal.startup.Startup.isJava6OrLater();
	}
}
