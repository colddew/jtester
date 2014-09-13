package org.jtester.module.spring.ibatis;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.orm.ibatis.SqlMapClientFactoryBean;
import org.springframework.util.ClassUtils;

import com.ibatis.sqlmap.client.SqlMapClient;

public class SqlMapClientFactoryBeanEx extends SqlMapClientFactoryBean {
	private final static boolean buildSqlMapClientWithInputStreamMethodAvailable = ClassUtils.hasMethod(
			SqlMapClientBuilderEx.class, "buildSqlMapClient", new Class[] { InputStream.class });

	private final static boolean buildSqlMapClientWithInputStreamAndPropertiesMethodAvailable = ClassUtils.hasMethod(
			SqlMapClientBuilderEx.class, "buildSqlMapClient", new Class[] { InputStream.class, Properties.class });

	private static final Map<String, SqlMapClient> sqlMapClients = new HashMap<String, SqlMapClient>();

	@Override
	protected SqlMapClient buildSqlMapClient(Resource configLocation, Properties properties) throws IOException {
		String configURI = configLocation.getURI().toString();
		SqlMapClient client = sqlMapClients.get(configURI);
		if (client == null) {
			InputStream is = configLocation.getInputStream();
			client = buildSqlMapClient(is, properties);
			sqlMapClients.put(configURI, client);
		}
		return client;
	}

	private SqlMapClient buildSqlMapClient(InputStream is, Properties properties) {
		if (properties != null) {
			if (buildSqlMapClientWithInputStreamAndPropertiesMethodAvailable) {
				return SqlMapClientBuilderEx.buildSqlMapClientByJTester(is, properties);
			} else {
				return SqlMapClientBuilderEx.buildSqlMapClient(new InputStreamReader(is), properties);
			}
		} else {
			if (buildSqlMapClientWithInputStreamMethodAvailable) {
				return SqlMapClientBuilderEx.buildSqlMapClient(is);
			} else {
				return SqlMapClientBuilderEx.buildSqlMapClient(new InputStreamReader(is));
			}
		}
	}
}
