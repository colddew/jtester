package org.jtester.module.core;

import java.lang.instrument.ClassDefinition;
import java.util.ArrayList;
import java.util.List;

import mockit.internal.startup.Startup;

import org.jtester.module.TestListener;
import org.jtester.module.core.helper.ConfigurationHelper;
import org.jtester.utility.JTesterLogger;

@SuppressWarnings("rawtypes")
public class ForbidModule implements Module {

	private List<Class> forbids = null;

	public void init() {
		this.forbids = new ArrayList<Class>();
		List<String> functions = ConfigurationHelper.getStringList(FORBID_FUNCTION);
		for (String function : functions) {
			String key = String.format("jtester.forbid.%s.implClass", function);
			String className = ConfigurationHelper.getString(key);
			try {
				Class clazz = Class.forName(className);
				forbids.add(clazz);
			} catch (ClassNotFoundException e) {
				JTesterLogger.warn("can't load forbid class, ClassNotFoundException:" + e.getLocalizedMessage());
			}
		}
	}

	public void afterInit() {
		try {
			ClassDefinition[] classDefs = this.definitionForbidClass();
			Startup.instrumentation().redefineClasses(classDefs);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public TestListener getTestListener() {
		return new ForbidTestListener();
	}

	public ClassDefinition[] definitionForbidClass() {
		if (forbids == null) {
			return new ClassDefinition[] {};
		}
		List<ClassDefinition> classDefinitions = new ArrayList<ClassDefinition>();
		for (Class clazz : forbids) {
			byte[] transformers = getForbidBytesByClass(clazz);
			ClassDefinition classDefinition = new ClassDefinition(clazz, transformers);

			classDefinitions.add(classDefinition);
		}

		return classDefinitions.toArray(new ClassDefinition[0]);
	}

	private byte[] getForbidBytesByClass(Class clazz) {
		// 改变过的class文件 TODO
		return null;
	}

	protected class ForbidTestListener extends TestListener {

		@Override
		protected String getName() {
			return "ForbidTestListener";
		}
	}
}
