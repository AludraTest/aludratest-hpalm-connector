package org.aludratest.hpalm.entity;

public class TestSetBuilder extends AbstractEntityBuilder {

	public TestSetBuilder() {
		super("test-set");
		setValue("subtype-id", "hp.qc.test-set.default");
	}

	public TestSetBuilder setParentId(long parentId) {
		setValue("parent-id", DF_INTEGER.format(parentId));
		return this;
	}

	public TestSetBuilder setName(String name) {
		setValue("name", name);
		return this;
	}

	public TestSetBuilder setSubtypeId(String subtypeId) {
		setValue("subtype-id", subtypeId);
		return this;
	}
}
