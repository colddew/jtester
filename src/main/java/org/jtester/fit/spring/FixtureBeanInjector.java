package org.jtester.fit.spring;

import static org.jtester.utility.AnnotationUtils.getFieldsAnnotatedWith;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import org.jtester.annotations.SpringBeanByName;
import org.jtester.annotations.SpringBeanByType;
import org.jtester.bytecode.reflector.helper.FieldHelper;
import org.jtester.exception.JTesterException;
import org.jtester.utility.StringHelper;
import org.springframework.context.ApplicationContext;

/**
 * 用在JTesterSpringFixture中注入spring bean
 * 
 * @author darui.wudr
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class FixtureBeanInjector {
	final ApplicationContext ctx;
	final Object testedObject;
	final Class testedClazz;

	public FixtureBeanInjector(final ApplicationContext ctx, Object testedObject) {
		this.ctx = ctx;
		this.testedObject = testedObject;
		if (testedObject == null) {
			throw new RuntimeException("tested object can't be null!");
		}
		this.testedClazz = testedObject.getClass();
	}

	public static void injectBeans(final ApplicationContext ctx, Object testedObject) {
		FixtureBeanInjector injector = new FixtureBeanInjector(ctx, testedObject);
		injector.injectBeans();
	}

	public void injectBeans() {
		injectSpringBeanByName(this.ctx, this.testedClazz, this.testedObject);
		injectSpringBeanByType(this.ctx, this.testedClazz, this.testedObject);
	}

	/**
	 * assign to fields by SpringBeanByName
	 */
	public static void injectSpringBeanByName(final ApplicationContext ctx, final Class testedClazz,
			final Object testedObject) {
		Set<Field> springBeanByNamefields = getFieldsAnnotatedWith(testedClazz, SpringBeanByName.class);
		for (Field field : springBeanByNamefields) {
			try {
				SpringBeanByName byName = field.getAnnotation(SpringBeanByName.class);
				String beanName = field.getName();
				if (StringHelper.isBlankOrNull(byName.value()) == false) {
					beanName = byName.value();
				}
				FieldHelper.setFieldValue(testedObject, field, ctx.getBean(beanName));
			} catch (Throwable e) {
				throw new JTesterException(
						"Unable to assign the Spring bean value to field annotated with @SpringBeanByName", e);
			}
		}
	}

	/**
	 * assign to fields by SpringBeanByType
	 */
	public static void injectSpringBeanByType(final ApplicationContext ctx, final Class testedClazz,
			final Object testedObject) {
		Set<Field> springBeanByTypeFields = getFieldsAnnotatedWith(testedClazz, SpringBeanByType.class);
		for (Field field : springBeanByTypeFields) {
			try {
				FieldHelper.setFieldValue(testedObject, field, getSpringBeanByType(ctx, field.getType()));
			} catch (Throwable e) {
				throw new JTesterException(
						"Unable to assign the Spring bean value to field annotated with @SpringBeanByType", e);
			}
		}
	}

	private static <T> T getSpringBeanByType(final ApplicationContext ctx, final Class<T> type) {
		Map<String, T> beans = ctx.getBeansOfType(type);
		if (beans == null || beans.size() == 0) {
			throw new JTesterException("Unable to get Spring bean by type. No Spring bean found for type "
					+ type.getSimpleName());
		}
		if (beans.size() > 1) {
			throw new JTesterException(
					"Unable to get Spring bean by type. More than one possible Spring bean for type "
							+ type.getSimpleName() + ". Possible beans; " + beans);
		}
		return beans.values().iterator().next();
	}
}
