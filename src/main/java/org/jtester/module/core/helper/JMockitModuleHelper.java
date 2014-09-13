package org.jtester.module.core.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import mockit.Mocked;
import mockit.NonStrict;

public class JMockitModuleHelper {
	/**
	 * 判断 @SpringBeanByName @SpringBeanByType 定义的字段是否和 @NonStrict @Mocked 定义在一起<br>
	 * 这种定义在逻辑意义上是无效的<br>
	 * 
	 * 如果碰到这种情况，抛出运行时异常
	 * 
	 * @param field
	 */
	public static void doesSpringBeanFieldIllegal(Field field) {
		Annotation[] annotations = field.getAnnotations();
		for (Annotation annotation : annotations) {
			if (NonStrict.class.isInstance(annotation)) {
				throw new RuntimeException(
						"@SpringBeanByName/@SpringBeanByType can't define with @NonStrict together. you may be hope to use @SpringBeanFrom @NonStrict.");
			}
			if (Mocked.class.isInstance(annotation)) {
				throw new RuntimeException(
						"@SpringBeanByName/@SpringBeanByType can't define with @Mocked together. you may be hope to use @SpringBeanFrom @Mocked.");
			}
		}
	}
}
