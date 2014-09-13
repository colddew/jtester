package org.jtester.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jtester.utility.ArrayHelper;

public class DataProviderIterator implements Iterator<Object[]> {
	private List<Object[]> datas = new ArrayList<Object[]>();
	private Iterator<Object[]> it = null;

	public void data(Object... data) {
		this.checkDataLength(data);
		this.datas.add(data);
		this.index++;
	}

	public boolean hasNext() {
		this.initIterator();
		return it.hasNext();
	}

	public Object[] next() {
		this.initIterator();
		return it.next();
	}

	public void remove() {
		this.initIterator();
		it.remove();
	}

	private synchronized void initIterator() {
		if (it == null) {
			it = this.datas.iterator();
		}
	}

	private int index = 1;
	private int prev = 0;

	private String ERROR_MSG = "DataProvider error, the previous data length is %d, but current data(data index %d) %s length is %d.";

	/**
	 * 检查数据长度是否一致
	 * 
	 * @param o
	 * @param data
	 */
	private void checkDataLength(Object... data) {
		int length = data.length;
		if (length == 0) {
			throw new RuntimeException(String.format("provider data(index %d) error, can't be empty.", index));
		}
		if (prev == 0 || prev == length) {
			prev = length;
		} else {
			String datas = ArrayHelper.toString(data);
			String error = String.format(ERROR_MSG, prev, index, datas, length);
			throw new RuntimeException(error);
		}
	}
}
