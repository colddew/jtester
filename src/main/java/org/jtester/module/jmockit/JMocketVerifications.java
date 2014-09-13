package org.jtester.module.jmockit;

import mockit.Verifications;

import org.jtester.bytecode.reflector.MethodAccessor;
import org.jtester.hamcrest.matcher.JMockitAdapter;

import ext.jtester.hamcrest.Matcher;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JMocketVerifications extends Verifications {

	public JMocketVerifications() {
		super();
		ExpectationsUtil.register(this);
	}

	public JMocketVerifications(int numberOfIterations) {
		super(numberOfIterations);
		ExpectationsUtil.register(this);
	}

	final static MethodAccessor methodAccessor = new MethodAccessor(Verifications.class, "addMatcher",
			mockit.external.hamcrest.Matcher.class);

	protected final <T> T with(Matcher argumentMatcher) {
		JMockitAdapter<T> adapter = JMockitAdapter.create(argumentMatcher);
		methodAccessor.invokeUnThrow(this, new Object[] { adapter });

		Object argValue = adapter.getInnerValue();
		return (T) argValue;
	}

	protected final <T> T with(T argValue, Matcher argumentMatcher) {
		JMockitAdapter<T> adapter = JMockitAdapter.create(argumentMatcher);
		methodAccessor.invokeUnThrow(this, new Object[] { adapter });
		return argValue;
	}
}
