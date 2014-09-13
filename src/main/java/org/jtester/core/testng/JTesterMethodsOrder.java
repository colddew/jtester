package org.jtester.core.testng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.internal.MethodInstance;

/**
 * 测试方法排序
 * 
 * @author darui.wudr
 * 
 */
public class JTesterMethodsOrder implements IMethodInterceptor {

	public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
		Map<String, List<IMethodInstance>> map = new HashMap<String, List<IMethodInstance>>();
		for (IMethodInstance mi : methods) {
			ITestNGMethod method = mi.getMethod();
			Object[] instances = mi.getInstances();
			if (instances == null || instances.length == 0) {
				continue;
			}
			Object instance = instances[0];
			String clazzname = instance.getClass().getName();

			List<IMethodInstance> list = map.get(clazzname);
			if (list == null) {
				list = new ArrayList<IMethodInstance>();
				map.put(clazzname, list);
			}
			if (instances.length == 1) {
				list.add(mi);
			} else {
				list.add(new MethodInstance(method, new Object[] { instance }));
			}
		}

		List<IMethodInstance> order = new ArrayList<IMethodInstance>();
		for (String clazzname : map.keySet()) {
			List<IMethodInstance> list = map.get(clazzname);
			order.addAll(list);
		}
		System.out.println("\n\n\n");
		for (IMethodInstance mi : order) {
			System.out.println(mi.getInstances()[0].getClass().getName());
		}
		System.out.println("\n\n\n");
		return order;
	}
}
