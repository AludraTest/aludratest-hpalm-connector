package org.aludratest.hpalm.entity;

import java.util.Date;

public final class TestRunBuilder extends AbstractEntityBuilder {

	public TestRunBuilder() {
		super("run");
		// HP does not seem to offer a subtype for AUTOMATED... strange.
		setSubtypeId("hp.qc.run.MANUAL");
	}

	public TestRunBuilder setName(String name) {
		setValue("name", name);
		return this;
	}

	public TestRunBuilder setTestInstanceId(long testInstanceId) {
		setValue("testcycl-id", DF_INTEGER.format(testInstanceId));
		setValue("test-instance", DF_INTEGER.format(testInstanceId));
		return this;
	}

	public TestRunBuilder setTestSetId(long testSetId) {
		setValue("cycle-id", DF_INTEGER.format(testSetId));
		return this;
	}

	public TestRunBuilder setTestId(long testId) {
		setValue("test-id", DF_INTEGER.format(testId));
		return this;
	}

	public TestRunBuilder setSubtypeId(String subtypeId) {
		setValue("subtype-id", subtypeId);
		return this;
	}

	public TestRunBuilder setStatus(String status) {
		setValue("status", status);
		return this;
	}

	public TestRunBuilder setOwner(String owner) {
		setValue("owner", owner);
		return this;
	}

	public TestRunBuilder setHost(String host) {
		setValue("host", host);
		return this;
	}

	public TestRunBuilder setComments(String comments) {
		setValue("comments", comments);
		return this;
	}

	public TestRunBuilder setDuration(long duration) {
		setValue("duration", DF_INTEGER.format(duration));
		return this;
	}

	public TestRunBuilder setExecutionDateAndTime(Date executionDateAndTime) {
		setValue("execution-date", DF_DATE.format(executionDateAndTime));
		setValue("execution-time", DF_TIME.format(executionDateAndTime));
		return this;
	}

	public TestRunBuilder setOSInfo(String osName, String osBuildNumber, String osServicePack) {
		setValue("os-name", osName);
		setValue("os-build", osBuildNumber);
		setValue("os-sp", osServicePack);
		return this;
	}

}
