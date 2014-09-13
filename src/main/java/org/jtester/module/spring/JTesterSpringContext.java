package org.jtester.module.spring;

import org.jtester.annotations.Tracer;
import org.jtester.core.TestedObject;
import org.jtester.module.core.helper.SpringModuleHelper;
import org.jtester.module.core.helper.TracerModuleHelper;
import org.jtester.module.spring.strategy.register.RegisterDynamicBean;
import org.jtester.utility.AnnotationUtils;
import org.jtester.utility.JTesterLogger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * {@link ClassPathXmlApplicationContext}的子类，运行使用@MockBean来替代spring中加载的bean值
 */
@SuppressWarnings({ "rawtypes" })
public class JTesterSpringContext extends ClassPathXmlApplicationContext {

	private final boolean ignoreNoSuchBean;

	public JTesterSpringContext(String[] configLocations, boolean refresh, ApplicationContext parent,
			boolean ignoreNoSuchBean) throws BeansException {
		super(configLocations, false, parent);
		this.ignoreNoSuchBean = ignoreNoSuchBean;
		if (refresh) {
			refresh();
		}
	}

	public JTesterSpringContext(Object testedObject, String[] configLocations, boolean ignoreNoSuchBean)
			throws BeansException {
		super(configLocations, false, null);
		this.ignoreNoSuchBean = ignoreNoSuchBean;
	}

	/**
	 * 将BeanFactory按JTesterBeanFactory类型返回
	 * 
	 * @return
	 */
	public final JTesterBeanFactory getJTesterBeanFactory() {
		ConfigurableListableBeanFactory beanFactory = super.getBeanFactory();
		return (JTesterBeanFactory) beanFactory;
	}

	@Override
	protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) super.obtainFreshBeanFactory();
		Class testedClazz = TestedObject.currTestedClazz();
		// 注册SpringBeanFrom的proxy bean
		SpringBeanFromFactory.registerSpringBeanFromField(beanFactory, testedClazz);

		JTesterLogger.info("Refresh spring classpath application context, tested class:" + testedClazz.getName());

		// @AutoInject生效时：@SpringBeanByName 和 @SpringBeanByType bean注册
		RegisterDynamicBean.dynamicRegisterBeanDefinition(beanFactory, testedClazz);

		// 是否定义bean输入输出跟踪日志
		boolean traceSpringBean = TracerModuleHelper.traceSpringBean();
		Tracer tracer = AnnotationUtils.getClassLevelAnnotation(Tracer.class, testedClazz);
		if ((tracer == null && traceSpringBean) || (tracer != null && tracer.spring())) {
			SpringModuleHelper.addTracerBeanDefinition(beanFactory);
		}
		return beanFactory;
	}

	@Override
	protected DefaultListableBeanFactory createBeanFactory() {
		BeanFactory parent = getInternalParentBeanFactory();
		return new JTesterBeanFactory(parent, ignoreNoSuchBean);
	}

	/**
	 * 下面这段本来想将spring初始化时所有的bean都置成lazy-init的模式<br>
	 * 但实现中碰到问题,主要是tracer的aop初始化上出错。
	 * 
	 * @Override protected void initBeanDefinitionReader(XmlBeanDefinitionReader
	 *           beanDefinitionReader) {
	 *           beanDefinitionReader.setEventListener(new
	 *           JTesterReaderEventListener()); }
	 * @Override protected void customizeBeanFactory(DefaultListableBeanFactory
	 *           beanFactory) { super.customizeBeanFactory(beanFactory);
	 *           beanFactory.setAllowEagerClassLoading(false); }
	 * 
	 * 
	 *           自定义spring ReaderEventListener<br>
	 *           参见{@link DefaultBeanDefinitionDocumentReader} 和
	 *           {@link BeanDefinitionParserDelegate}的initDefaults方法
	 * 
	 *           <pre>
	 * 复写defaultsRegistered方法，在跑单元测试中，强制设置default-lazy-init=true属性
	 * </pre>
	 * 
	 *           public static class JTesterReaderEventListener extends
	 *           EmptyReaderEventListener {
	 * @Override public void defaultsRegistered(DefaultsDefinition
	 *           defaultsDefinition) { if (defaultsDefinition instanceof
	 *           DocumentDefaultsDefinition) { DocumentDefaultsDefinition
	 *           docDefault = (DocumentDefaultsDefinition) defaultsDefinition;
	 *           docDefault.setLazyInit("true"); } } }
	 **/
}