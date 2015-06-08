package org.aludratest.hpalm.entity;

public enum RunStepStatus {

	BLOCKED("Blocked", "hp.qc.status.blocked"), FAILED("Failed", "hp.qc.status.failed"), N_A("N/A", "hp.qc.status.n-a"), NO_RUN(
			"No Run", "hp.qc.status.no-run"), NOT_COMPLETED("Not Completed", "hp.qc.status.not-completed"), PASSED("Passed",
			"hp.qc.status.passed");

	private String displayName;

	private String logicalName;

	private RunStepStatus(String displayName, String logicalName) {
		this.displayName = displayName;
		this.logicalName = logicalName;
	}

	public String displayName() {
		return displayName;
	}

	public String logicalName() {
		return logicalName;
	}

}
