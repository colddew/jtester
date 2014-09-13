package org.jtester.fit.fixture;

import org.jtester.annotations.SpringApplicationContext;
import org.jtester.core.IJTester;
import org.jtester.core.context.DbFitContext;
import org.jtester.core.context.DbFitContext.RunIn;
import org.jtester.fit.JTesterFixture;
import org.jtester.fit.spring.FixtureBeanInjector;
import org.jtester.fit.spring.RemoteInvokerRegister;
import org.jtester.fit.spring.FixtureSpringApplicationContext;
import org.jtester.utility.AnnotationUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringFixture extends JTesterFixture implements IJTester {
	private ClassPathXmlApplicationContext ctx;

	/**
	 * 注入spring bean
	 */
	public SpringFixture() {
		DbFitContext.setRunIn(RunIn.FitNesse);

		SpringApplicationContext anotations = AnnotationUtils.getClassLevelAnnotation(SpringApplicationContext.class,
				this.getClass());
		if (anotations == null) {
			return;
		}
		try {
			String[] locations = anotations.value();
			boolean ignoreNoSuchBean = anotations.ignoreNoSuchBean();
			ctx = new FixtureSpringApplicationContext(locations, this.getClass(), ignoreNoSuchBean);
			FixtureBeanInjector.injectBeans(ctx, this);
			RemoteInvokerRegister.injectSpringBeanRemote(ctx, this);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException("spring inject error", e);
		}
	}
}
