package org.jtester.module.jmockit;

import mockit.Expectations;
import mockit.Mocked;
import mockit.internal.expectations.transformation.ActiveInvocations;

import org.jtester.bytecode.reflector.MethodAccessor;
import org.jtester.hamcrest.TheStyleAssertion;
import org.jtester.hamcrest.matcher.JMockitAdapter;

import ext.jtester.hamcrest.Matcher;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JMockitExpectations extends Expectations implements JTesterInvocations {
	@Mocked(methods = { "" })
	protected InvokeTimes invokerTimes;

	@Mocked(methods = { "" })
	protected ExpectationsResult expectationsResult;

	@Mocked(methods = { "" })
	protected final TheStyleAssertion the;

	public JMockitExpectations() {
		super();
		ExpectationsUtil.register(this);
		this.the = new TheStyleAssertion();
	}

	public JMockitExpectations(int numberOfIterations, Object... classesOrObjectsToBePartiallyMocked) {
		super(numberOfIterations, classesOrObjectsToBePartiallyMocked);
		ExpectationsUtil.register(this);
		this.the = new TheStyleAssertion();
	}

	public JMockitExpectations(Object... classesOrObjectsToBePartiallyMocked) {
		super(classesOrObjectsToBePartiallyMocked);
		ExpectationsUtil.register(this);
		this.the = new TheStyleAssertion();
	}

	public <T> InvokeTimes when(T o) {
		return new InvokeTimes(this);
	}

	/**
	 * @deprecated <br>
	 *             please use thenReturn(value)
	 */
	@Deprecated
	public void returnValue(Object value) {
		super.returns(value);
	}

	public void thenReturn(Object value) {
		super.returns(value);
	}

	/**
	 * deprecated<br>
	 * please use thenThrow(e)
	 * 
	 * @param e
	 */
	@Deprecated
	public void throwException(Throwable e) {
		ActiveInvocations.addResult(e);
	}

	public void thenThrow(Throwable e) {
		ActiveInvocations.addResult(e);
	}

	/**
	 * @deprecated <br>
	 *             please use thenReturn(value...)
	 */
	@Deprecated
	public void returnValue(Object firstValue, Object... remainingValues) {
		super.returns(firstValue, remainingValues);
	}

	public void thenReturn(Object firstValue, Object... remainingValues) {
		super.returns(firstValue, remainingValues);
	}

	public void thenDoing(Delegate delegate) {
		super.returns(delegate);
	}

	public <T> T any(Class<T> claz) {
		T o = the.object().any().wanted(claz);
		return o;
	}

	public <T> T is(T value) {
		T o = (T) the.object().reflectionEq(value).wanted();
		return o;
	}

	final static MethodAccessor methodAccessor = new MethodAccessor(Expectations.class, "addMatcher",
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

	public static interface Delegate extends mockit.Delegate {
	}
}
