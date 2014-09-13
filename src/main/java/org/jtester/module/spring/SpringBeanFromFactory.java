package org.jtester.module.spring;

import static org.jtester.utility.AnnotationUtils.getFieldsAnnotatedWith;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

import org.jtester.annotations.SpringBeanFrom;
import org.jtester.bytecode.imposteriser.JTesterProxy;
import org.jtester.bytecode.reflector.helper.FieldHelper;
import org.jtester.core.TestedObject;
import org.jtester.exception.FindBeanImplClassException;
import org.jtester.utility.JTesterLogger;
import org.jtester.utility.StringHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SpringBeanFromFactory implements FactoryBean, BeanFactoryAware {

	public String fieldName;

	private JTesterBeanFactory beanFactory;

	private Class type = null;

	public SpringBeanFromFactory() {
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getObject() throws Exception {
		Class testClazz = TestedObject.currTestedClazz();
		Object o = JTesterProxy.proxy(testClazz, fieldName);
		return o;
	}

	public Class getObjectType() {
		if (type == null && this.beanFactory != null) {
			Class testClazz = TestedObject.currTestedClazz();

			Field field = FieldHelper.getField(testClazz, this.fieldName);
			this.type = field.getType();
		}
		return type;
	}

	public boolean isSingleton() {
		return true;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (JTesterBeanFactory) beanFactory;
	}

	/**
	 * 注册tested object下所有的@SpringBeanFrom对象<br>
	 * 
	 * @param testedObject
	 */
	public static void registerSpringBeanFromField(final DefaultListableBeanFactory beanFactory, Class testedClazz) {
		Set<Field> beanFields = getFieldsAnnotatedWith(testedClazz, SpringBeanFrom.class);

		for (Field beanField : beanFields) {
			SpringBeanFrom beanFor = beanField.getAnnotation(SpringBeanFrom.class);
			String fieldName = beanField.getName();
			String beanName = beanFor.value();
			if (StringHelper.isBlankOrNull(beanName)) {
				beanName = fieldName;
			}
			registerProxyBeanDefinition(beanFactory, beanName, fieldName);
		}
	}

	/**
	 * 定义SpringBeanFor的代理proxy bean
	 * 
	 * @param beanFactory
	 * @param beanName
	 * @param byName
	 * @throws FindBeanImplClassException
	 */
	private static void registerProxyBeanDefinition(final DefaultListableBeanFactory beanFactory,
			final String beanName, final String fieldName) {
		// SpringBeanFrom采用的是覆盖原有的bean定义
		if (beanFactory.containsBeanDefinition(beanName)) {
			JTesterLogger
					.info(String
							.format("SpringBeanFrom BeanName[%s] has been defined in application context, so override bean definition.",
									beanName));
		}

		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClassName(SpringBeanFromFactory.class.getName());
		beanDefinition.setScope("singleton");
		beanDefinition.setAutowireCandidate(true);
		beanDefinition.setLazyInit(true);

		beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);

		MutablePropertyValues properties = new MutablePropertyValues(new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				this.put("fieldName", fieldName);
			}
		});
		beanDefinition.setPropertyValues(properties);
		beanFactory.registerBeanDefinition(beanName, beanDefinition);
	}
}
