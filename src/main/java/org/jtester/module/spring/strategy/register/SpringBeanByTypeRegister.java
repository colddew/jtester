package org.jtester.module.spring.strategy.register;

import java.lang.reflect.Field;
import java.util.Set;

import org.jtester.annotations.SpringBeanByType;
import org.jtester.utility.AnnotationUtils;

@SuppressWarnings({ "unchecked", "rawtypes" })
class SpringBeanByTypeRegister extends SpringBeanRegister {

	@Override
	protected Set<Field> getRegisterField(Class testedClazz) {
		Set<Field> fields = AnnotationUtils.getFieldsAnnotatedWith(testedClazz, SpringBeanByType.class);
		return fields;
	}

	@Override
	protected void initSpringBean(final Field field, final BeanMeta beanMeta) {

		SpringBeanByType byType = field.getAnnotation(SpringBeanByType.class);

		beanMeta.beanClaz = byType.value();
		beanMeta.initMethod = byType.init();
		beanMeta.beanName = field.getName();
		beanMeta.properties = byType.properties();
	}
}
