package org.jtester.module.spring.ibatis;

import java.io.InputStream;
import java.util.Properties;

import org.jtester.bytecode.reflector.FieldAccessor;
import org.jtester.module.core.helper.ConfigurationHelper;
import org.jtester.utility.JTesterLogger;
import org.w3c.dom.Node;

import com.ibatis.common.resources.Resources;
import com.ibatis.common.xml.Nodelet;
import com.ibatis.common.xml.NodeletUtils;
import com.ibatis.sqlmap.client.SqlMapException;
import com.ibatis.sqlmap.engine.builder.xml.SqlMapConfigParser;
import com.ibatis.sqlmap.engine.builder.xml.SqlMapParser;
import com.ibatis.sqlmap.engine.builder.xml.XmlParserState;

public class SqlMapConfigParserEx extends SqlMapConfigParser {

	@SuppressWarnings("rawtypes")
	private static final FieldAccessor stateField = new FieldAccessor(
			com.ibatis.sqlmap.engine.builder.xml.SqlMapConfigParser.class, "state");

	private XmlParserState state = new XmlParserState();

	@Override
	protected void addSqlMapNodelets() {
		this.state = (XmlParserState) stateField.get(this);
		parser.addNodelet("/sqlMapConfig/sqlMap", new JTesterIbatisNodelet(this.state));
	}

	private static class JTesterIbatisNodelet implements Nodelet {
		private XmlParserState state = new XmlParserState();

		protected JTesterIbatisNodelet(XmlParserState state) {
			this.state = state;
		}

		public void process(Node node) {
			String resource = null;
			try {
				state.getConfig().getErrorContext().setActivity("loading the SQL Map resource");

				Properties attributes = NodeletUtils.parseAttributes(node, state.getGlobalProps());

				resource = attributes.getProperty("resource");
				String url = attributes.getProperty("wikiUrl");

				InputStream inputStream = null;
				if (resource != null) {
					state.getConfig().getErrorContext().setResource(resource);
					inputStream = Resources.getResourceAsStream(resource);
				} else if (url != null) {
					state.getConfig().getErrorContext().setResource(url);
					inputStream = Resources.getUrlAsStream(url);
				} else {
					throw new SqlMapException("The <sqlMap> element requires either a resource or a url attribute.");
				}
				new SqlMapParser(state).parse(inputStream);
			} catch (Throwable e) {
				String error = String.format("loading sql map resource[%s] error:%s.", resource, e.getMessage());
				boolean throwException = ConfigurationHelper
						.getBoolean(ConfigurationHelper.IBATIS_SQLMAP_THROW_EXCEPTION);
				if (throwException) {
					throw new RuntimeException(error, e);
				} else {
					JTesterLogger.warn(error);
				}
			}
		}
	}
}
