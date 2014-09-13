package org.jtester.json.decoder.array;

import java.util.Map;

import org.jtester.json.JSON;
import org.jtester.json.decoder.ArrayDecoder;
import org.jtester.json.helper.JSONObject;

public class DoubleArrayDecoder extends ArrayDecoder<double[]> {
	public final static DoubleArrayDecoder instance = new DoubleArrayDecoder();

	private DoubleArrayDecoder() {
		super(double.class);
	}

	@Override
	protected double[] newArraysObject(int size) {
		return new double[size];
	}

	@Override
	protected void setObjectByIndex(double[] array, int index, JSONObject json, Map<String, Object> references) {
		double value = (Double)JSON.toObject(json, double.class, references);
		array[index] = value;
	}
}
