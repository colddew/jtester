package org.jtester.module.dbfit;

import org.jtester.core.testng.TestNgUtil;

public class DbUnitTestFixture extends DatabaseFixture {
	public boolean testng(String clazz, String method) {
		return TestNgUtil.run(clazz, method, false);
	}
}
