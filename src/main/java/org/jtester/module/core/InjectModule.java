package org.jtester.module.core;

import static org.jtester.utility.AnnotationUtils.getFieldsAnnotatedWith;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.jtester.annotations.Inject;
import org.jtester.bytecode.imposteriser.JTesterProxy;
import org.jtester.bytecode.reflector.PropertyAccessor;
import org.jtester.bytecode.reflector.helper.ClazzHelper;
import org.jtester.module.TestListener;
import org.jtester.module.core.helper.InjectionModuleHelper;
import org.jtester.utility.StringHelper;

@SuppressWarnings("rawtypes")
public class InjectModule implements Module {
	public void init() {
	}

	public void afterInit() {
	}

	/**
	 * jtester扩展的注入<br>
	 * 
	 * @org.jtester.unitils.jmock.Mock<br> <br>
	 * @org.jtester.unitils.inject.Inject
	 * 
	 * @param testedObject
	 */
	private void jtesterInject(Object testedObject) {
		Set<Field> injects = getFieldsAnnotatedWith(testedObject.getClass(), Inject.class);
		for (Field injectField : injects) {
			Class injectedClazz = injectField.getType();
			Inject inject = injectField.getAnnotation(Inject.class);

			Object injectedObject = JTesterProxy.proxy(testedObject.getClass(), injectField);
			injectedInto(testedObject, injectedObject, injectedClazz, inject.targets(), inject.properties());
		}
	}

	/**
	 * 把对象injectedObject注入到testedObject对应的变量targets的属性中
	 * 
	 * @param testedObject
	 *            测试类实例
	 * @param injectedObject
	 *            被注入实例
	 * @param injectedClazz
	 *            被注入实例定义类型
	 * @param targets
	 *            要注入的对象列表
	 * @param properties
	 *            注入到对象的那个属性中列表；如果属性为空，则按类型注入
	 */
	private static void injectedInto(Object testedObject, Object injectedObject, Class injectedClazz, String[] targets,
			String[] properties) {
		for (int index = 0; index < targets.length; index++) {
			String target = targets[index];
			Object targetObject = PropertyAccessor.getPropertyByOgnl(testedObject, target, true);
			targetObject = ClazzHelper.getProxiedObject(targetObject);
			if (targetObject == null) {
				throw new RuntimeException("can't inject a mock object into a null object, ongl = " + target);
			}
			String property = index < properties.length ? properties[index] : null;
			if (StringHelper.isBlankOrNull(property)) {
				InjectionModuleHelper.injectIntoByType(injectedObject, injectedClazz == null ? targetObject.getClass()
						: injectedClazz, targetObject);
			} else {
				InjectionModuleHelper.injectInto(injectedObject, targetObject, property);
			}
		}
	}

	public TestListener getTestListener() {
		return new InjectTestListener();
	}

	protected class InjectTestListener extends TestListener {
		@Override
		public void beforeMethodRunning(Object testObject, Method testMethod) {
			jtesterInject(testObject);
		}

		@Override
		protected String getName() {
			return "InjectTestListener";
		}
	}

}
