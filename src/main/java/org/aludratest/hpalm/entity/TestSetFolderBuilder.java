package org.aludratest.hpalm.entity;

public class TestSetFolderBuilder extends AbstractEntityBuilder {

	public TestSetFolderBuilder() {
		super("test-set-folder");
	}

	public TestSetFolderBuilder setParentId(long id) {
		setValue("parent-id", DF_INTEGER.format(id));
		return this;
	}

	public TestSetFolderBuilder setName(String name) {
		setValue("name", name);
		return this;
	}

}
