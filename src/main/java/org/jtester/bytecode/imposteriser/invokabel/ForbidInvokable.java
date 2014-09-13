package org.jtester.bytecode.imposteriser.invokabel;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.jtester.bytecode.imposteriser.Invocation;
import org.jtester.bytecode.imposteriser.Invokable;
import org.jtester.exception.ForbidCallException;

/**
 * 禁止访问类的代理器<br>
 * 只有调用禁止类的任何操作就会抛出ForbidCallException
 * 
 * @author darui.wudr
 * 
 */
@SuppressWarnings("rawtypes")
public class ForbidInvokable implements Invokable {

	private final Class clazz;

	public ForbidInvokable(Class clazz) {
		this.clazz = clazz;
	}

	public Object invoke(Invocation invocation) throws Throwable {
		Method method = invocation.getInvokedMethod();
		String methodname = method.getName();
		if (methodname.equalsIgnoreCase("getClass")) {
			System.out.println("=============getClass()");
			return clazz;
		} else if (filter.contains(methodname)) {
			Object target = invocation.getInvokedObject();
			Object[] paras = invocation.getParametersAsArray();

			return method.invoke(target, paras);
		} else {
			throw new ForbidCallException("this a forbid class to be called.");
		}
	}

	private static Set<String> filter = new HashSet<String>() {
		private static final long serialVersionUID = 1L;

		{
			// this.add("getClass");
			this.add("hashCode");
			this.add("finalize");
			this.add("equals");
			this.add("toString");
		}
	};
}
