package org.jtester.module.spring;

import javax.sql.DataSource;

import org.jtester.module.core.helper.ConfigurationHelper;
import org.jtester.module.database.environment.DBEnvironmentFactory;
import org.jtester.module.tracer.TracerBeanManager;
import org.jtester.utility.JTesterLogger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

@SuppressWarnings("rawtypes")
public class JTesterBeanFactory extends DefaultListableBeanFactory {

	private boolean ignoreNoSuchBean;

	public JTesterBeanFactory(final BeanFactory parentBeanFactory, boolean ignoreNoSuchBean) {
		super(parentBeanFactory);
		this.ignoreNoSuchBean = ignoreNoSuchBean;
	}

	@Override
	public Object getBean(final String name, final Class requiredType, final Object[] args) throws BeansException {
		try {
			Object bean = getMyBean(name, requiredType, args);
			return bean;
		} catch (NoSuchBeanDefinitionException e) {
			if (ignoreNoSuchBean) {
				JTesterLogger.info("Ignore NoSuchBeanDefinitionException:" + e.getMessage());
				return null;
			} else {
				throw e;
			}
		}
	}

	private Object getMyBean(final String name, final Class requiredType, final Object[] args) throws BeansException {

		if (ConfigurationHelper.isSpringDataSourceName(name)) {
			DataSource dataSource = DBEnvironmentFactory.getDBEnvironment().getDataSource(false);
			return dataSource;
		} else {
			Object bean = super.getBean(name, requiredType, args);
			return bean;
		}
	}

	@Override
	public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) {
		TracerBeanManager.registerBean(existingBean, beanName);
		return super.applyBeanPostProcessorsBeforeInitialization(existingBean, beanName);
	}
}
