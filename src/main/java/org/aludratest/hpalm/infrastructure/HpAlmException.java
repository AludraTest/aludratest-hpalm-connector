package org.aludratest.hpalm.infrastructure;

public class HpAlmException extends Exception {

	private static final long serialVersionUID = 4846509671368177356L;

	private String id;

	public HpAlmException(String message) {
		super(message);
	}

	public HpAlmException(String message, String id) {
		super(message);
		this.id = id;
	}

	public HpAlmException(String message, Throwable cause) {
		super(message, cause);
	}

	public String getHpAlmId() {
		return id;
	}

}
