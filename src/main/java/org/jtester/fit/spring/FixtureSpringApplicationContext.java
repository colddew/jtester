package org.jtester.fit.spring;

import org.jtester.module.spring.JTesterSpringContext;
import org.jtester.module.spring.strategy.register.RegisterDynamicBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import fit.Fixture;

/**
 * 
 * @author darui.wudr
 * 
 */
public class FixtureSpringApplicationContext extends JTesterSpringContext {
	final Class<? extends Fixture> fixtureClazz;

	public FixtureSpringApplicationContext(String[] configLocations, final Class<? extends Fixture> fixtureClazz,
			boolean ignoreNoSuchBean) throws BeansException {
		super(configLocations, false, null, ignoreNoSuchBean);
		this.fixtureClazz = fixtureClazz;
		refresh();
	}

	@Override
	protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) super.obtainFreshBeanFactory();

		// Fixture暂不能mock任何东西
		// MockBeanRegister.registerAllMockBean(fixtureClazz);

		RegisterDynamicBean.dynamicRegisterBeanDefinition(beanFactory, fixtureClazz);
		// 增加httpInovoke的配置
		RemoteInvokerRegister.registerSpringBeanRemoteOnClient(beanFactory, fixtureClazz);

		return beanFactory;
	}
}
