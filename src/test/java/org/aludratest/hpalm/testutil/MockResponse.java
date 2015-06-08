package org.aludratest.hpalm.testutil;

import java.util.LinkedHashMap;
import java.util.Map;

public class MockResponse {

	private Map<String, String> headers = new LinkedHashMap<String, String>();

	private int returnCode;

	private String data;

	public void setHeader(String header, String value) {
		headers.put(header, value);
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public String getData() {
		return data;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

}
