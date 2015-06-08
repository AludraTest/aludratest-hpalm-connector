package org.aludratest.hpalm.entity;

public class TestInstanceBuilder extends AbstractEntityBuilder {

	public TestInstanceBuilder() {
		super("test-instance");
		setValue("subtype-id", "hp.qc.test-instance.MANUAL");
	}

	public TestInstanceBuilder setTestSetId(long testSetId) {
		setValue("cycle-id", DF_INTEGER.format(testSetId));
		return this;
	}

	public TestInstanceBuilder setTestId(long testId) {
		setValue("test-id", DF_INTEGER.format(testId));
		return this;
	}

	public TestInstanceBuilder setTestConfigId(long testConfigId) {
		setValue("test-config-id", DF_INTEGER.format(testConfigId));
		return this;
	}

	public TestInstanceBuilder setOrderNumber(long orderNumber) {
		setValue("test-order", DF_INTEGER.format(orderNumber));
		return this;
	}

	public TestInstanceBuilder setStatus(String status) {
		setValue("status", status);
		return this;
	}

	public TestInstanceBuilder setExecDateTimeFromEntity(Entity entity) {
		setValue("exec-date", entity.getStringFieldValue("exec-date"));
		setValue("exec-time", entity.getStringFieldValue("exec-time"));
		return this;
	}
}
