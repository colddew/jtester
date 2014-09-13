package org.jtester.module.spring.ibatis;

import java.io.InputStream;
import java.util.Properties;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.ibatis.sqlmap.engine.builder.xml.SqlMapConfigParser;

public class SqlMapClientBuilderEx extends SqlMapClientBuilder {
	/**
	 * 构造jtester的SqlMapConfigParser来构造SqlMapClient
	 * 
	 * @param inputStream
	 * @param props
	 * @return
	 */
	public static SqlMapClient buildSqlMapClientByJTester(InputStream inputStream, Properties props) {
		// return new SqlMapConfigParserEx().parse(inputStream, props);
		return new SqlMapConfigParser().parse(inputStream, props);
	}
}
