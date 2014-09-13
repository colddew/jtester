package org.jtester.module.database.dbop;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jtester.exception.ExceptionWrapper;
import org.jtester.module.database.util.SqlRunner;

public class SqlSet {
	private List<String> list = new ArrayList<String>();

	public void sql(String sql) {
		this.list.add(sql);
	}

	public void readFrom(String filename) {
		try {
			SqlRunner.executeFromFile(filename);
		} catch (Exception e) {
			throw ExceptionWrapper.wrapWithRuntimeException(e);
		}
	}

	public void readFrom(File file) {
		try {
			SqlRunner.executeFromStream(new FileInputStream(file));
		} catch (Exception e) {
			throw ExceptionWrapper.wrapWithRuntimeException(e);
		}
	}

	public void readFrom(InputStream stream) {
		try {
			SqlRunner.executeFromStream(stream);
		} catch (Exception e) {
			throw ExceptionWrapper.wrapWithRuntimeException(e);
		}
	}

	/**
	 * 执行列表中的sql语句<br>
	 * 执行完毕，列表不做清空，方便重用
	 */
	public void execute() {
		for (String sql : list) {
			SqlRunner.execute(sql);
		}
	}
}
