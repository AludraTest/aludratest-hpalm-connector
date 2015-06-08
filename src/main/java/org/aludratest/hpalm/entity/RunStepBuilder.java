package org.aludratest.hpalm.entity;

import java.util.Date;

public final class RunStepBuilder extends AbstractEntityBuilder {

	public RunStepBuilder() {
		super("run-step");
	}

	public RunStepBuilder setTestRunId(long testRunId) {
		setValue("parent-id", DF_INTEGER.format(testRunId));
		return this;
	}

	public RunStepBuilder setName(String name) {
		setValue("name", name);
		return this;
	}

	public RunStepBuilder setStatus(RunStepStatus status) {
		setValue("status", status.displayName());
		return this;
	}

	public RunStepBuilder setExecutionDateTime(Date dateTime) {
		setValue("execution-date", DF_DATE.format(dateTime));
		setValue("execution-time", DF_TIME.format(dateTime));
		return this;
	}

	public RunStepBuilder setDescription(String description) {
		setValue("description", description);
		return this;
	}

}
