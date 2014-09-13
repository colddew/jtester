package org.jtester.module.database.util;

import org.jtester.exception.UnConfigDataBaseTypeException;
import org.jtester.module.core.helper.ConfigurationHelper;
import org.jtester.module.database.support.Db2DbSupport;
import org.jtester.module.database.support.DbSupport;
import org.jtester.module.database.support.DerbyDbSupport;
import org.jtester.module.database.support.H2DbSupport;
import org.jtester.module.database.support.HsqldbDbSupport;
import org.jtester.module.database.support.MsSqlDbSupport;
import org.jtester.module.database.support.MySqlDbSupport;
import org.jtester.module.database.support.OracleDbSupport;
import org.jtester.utility.StringHelper;

public enum DataSourceType {
	/**
	 * H2Db<br>
	 * "org.hibernate.dialect.H2Dialect", "public", "public"
	 */
	H2DB() {
		@Override
		public DbSupport getDbSupport() {
			return new H2DbSupport();
		}

		@Override
		public boolean autoExport() {
			return true;
		}

		@Override
		public boolean isMemoryDB() {
			return true;
		}
	},
	/**
	 * HsqlDb<br>
	 * "org.hibernate.dialect.HSQLDialect", "public", "public"
	 */
	HSQLDB() {
		@Override
		public DbSupport getDbSupport() {
			return new HsqldbDbSupport();
		}

		@Override
		public boolean autoExport() {
			return true;
		}

		@Override
		public boolean isMemoryDB() {
			return true;
		}
	},
	MYSQL() {
		@Override
		public DbSupport getDbSupport() {
			return new MySqlDbSupport();
		}
	},
	ORACLE() {
		@Override
		public DbSupport getDbSupport() {
			return new OracleDbSupport();
		}
	},
	SQLSERVER() {
		@Override
		public DbSupport getDbSupport() {
			return new MsSqlDbSupport();
		}
	},

	DERBYDB() {
		@Override
		public DbSupport getDbSupport() {
			return new DerbyDbSupport();
		}
	},

	DB2() {
		@Override
		public DbSupport getDbSupport() {
			return new Db2DbSupport();
		}
	},

	UNSUPPORT() {
		@Override
		public DbSupport getDbSupport() {
			throw new RuntimeException("unsupport database type");
		}
	};

	private String hibernate_dialect = null;

	private String infoSchema = null;

	private DataSourceType() {
	}

	public String getHibernateDialect() {
		return this.hibernate_dialect;
	}

	public abstract DbSupport getDbSupport();

	public String getInfoSchema() {
		return this.infoSchema;
	}

	public boolean isMemoryDB() {
		return false;
	}

	public boolean autoExport() {
		return ConfigurationHelper.autoExport();
	}

	/**
	 * 根据配置查找对应的数据库类型<br>
	 * type=null || "",表示配置文件中设置的默认数据库
	 * 
	 * @param type
	 * @return
	 */
	public static DataSourceType databaseType(final String type) {
		String _type = type;
		if (StringHelper.isBlankOrNull(type)) {
			_type = ConfigurationHelper.databaseType();
		}
		if (StringHelper.isBlankOrNull(_type)) {
			throw new UnConfigDataBaseTypeException("please config property 'database.type'");
		}
		try {
			DataSourceType dbType = DataSourceType.valueOf(_type.toUpperCase());

			return dbType;
		} catch (Throwable e) {
			throw new RuntimeException("unknown database type", e);
		}
	}
}
