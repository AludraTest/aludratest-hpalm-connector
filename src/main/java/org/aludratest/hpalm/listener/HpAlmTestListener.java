/*
 * Copyright (C) 2015 Hamburg Sud and the contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aludratest.hpalm.listener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aludratest.config.ConfigurationException;
import org.aludratest.hpalm.TestCaseIdResolver;
import org.aludratest.hpalm.entity.AbstractEntityBuilder;
import org.aludratest.hpalm.entity.Entity;
import org.aludratest.hpalm.entity.RunStepBuilder;
import org.aludratest.hpalm.entity.RunStepStatus;
import org.aludratest.hpalm.entity.TestInstanceBuilder;
import org.aludratest.hpalm.entity.TestRunBuilder;
import org.aludratest.hpalm.impl.HpAlmConfiguration;
import org.aludratest.hpalm.infrastructure.EntityCollection;
import org.aludratest.hpalm.infrastructure.HpAlmException;
import org.aludratest.hpalm.infrastructure.HpAlmSession;
import org.aludratest.hpalm.infrastructure.HpAlmUtil;
import org.aludratest.scheduler.AbstractRunnerListener;
import org.aludratest.scheduler.RunnerListener;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.util.CommonRunnerLeafAttributes;
import org.aludratest.service.ElementName;
import org.aludratest.service.ElementType;
import org.aludratest.service.TechnicalArgument;
import org.aludratest.service.TechnicalLocator;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testcase.event.TestStepInfo;
import org.aludratest.testcase.event.attachment.Attachment;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.databene.formats.html.util.HTMLUtil;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(role = RunnerListener.class, hint = "hpalm")
public class HpAlmTestListener extends AbstractRunnerListener {

	private static final Logger LOG = LoggerFactory.getLogger(HpAlmTestListener.class);

	private static final String[] testStepColumns = new String[] { "Started", "Command", "Element Type", "Element Name", "Data",
			"Error Message", "Technical Locator", "Technical Arguments", "Comment" };

	private static final Map<TestStatus, String> cellStyle = new EnumMap<TestStatus, String>(TestStatus.class);
	static {
		cellStyle.put(TestStatus.PASSED, "style=\"background-color: #BBFFCC\"");
		cellStyle.put(TestStatus.FAILED, "style=\"background-color: #FFAAAA\"");
		cellStyle.put(TestStatus.INCONCLUSIVE, "style=\"background-color: #88BBFF\"");
		cellStyle.put(TestStatus.IGNORED, "style=\"background-color: #BBBBBB\"");
		cellStyle.put(TestStatus.FAILEDACCESS, "style=\"background-color: #EE88FF\"");
		cellStyle.put(TestStatus.FAILEDPERFORMANCE, "style=\"background-color: #DDAAFF\"");
		cellStyle.put(TestStatus.FAILEDAUTOMATION, "style=\"background-color: #FFFFAA\"");
	}

	@Requirement
	private TestCaseIdResolver idResolver;

	@Requirement
	private HpAlmConfiguration configuration;

	private HpAlmWorkerThread workerThread;

	private TestCaseIdResolver getIdResolver() {
		return idResolver;
	}

	@Override
	public void startingTestProcess(RunnerTree runnerTree) {
		if (configuration.isEnabled()) {
			if (getIdResolver() == null) {
				LOG.error("No IdResolver registered for HP ALM connector. Connector is disabled now.");
			}
			else {
				workerThread = new HpAlmWorkerThread(configuration);
				workerThread.start();
			}
		}
	}

	@Override
	public void startingTestLeaf(RunnerLeaf runnerLeaf) {
		if (!configuration.isEnabled() || getIdResolver() == null) {
			return;
		}
		Long id = getIdResolver().getHpAlmTestId(runnerLeaf);
		if (id == null) {
			return;
		}

		Long configId = getIdResolver().getHpAlmTestConfigId(runnerLeaf);

		TestCaseData data = new TestCaseData();
		TestRunBuilder builder = new TestRunBuilder();
		builder.setTestId(id.longValue());
		if (Boolean.TRUE.equals(runnerLeaf.getAttribute(CommonRunnerLeafAttributes.IGNORE))) {
			builder.setStatus(RunStepStatus.BLOCKED.displayName());
		}
		else {
			builder.setStatus(RunStepStatus.PASSED.displayName());
		}

		data.startTime = new DateTime();
		data.testRunBuilder = builder;
		data.hpAlmId = id.longValue();
		data.hpAlmConfigId = configId;
		runnerLeaf.setAttribute("hpalmData", data);
	}

	@Override
	public void newTestStepGroup(RunnerLeaf runnerLeaf, String groupName) {
		TestCaseData data = (TestCaseData) runnerLeaf.getAttribute("hpalmData");
		if (data == null) {
			return;
		}

		RunStepBuilder builder = new RunStepBuilder();

		// time zone must be initialized before setExecutionDateTime can be called
		while (!workerThread.isTimeZoneInitialized()) {
			synchronized (workerThread) {
				try {
					workerThread.wait();
				}
				catch (InterruptedException e) {
					return;
				}
			}
		}

		builder.setName(groupName).setExecutionDateTime(new Date())
				.setStatus(data.alreadyFailed ? RunStepStatus.NO_RUN : RunStepStatus.PASSED);
		
		TestStepData step = new TestStepData();
		step.runStepBuilder = builder;

		// start description of the step as HTML
		step.sbDescription = new StringBuilder();
		step.sbDescription.append("<html><head></head><body><table><tr ").append(cellStyle.get(TestStatus.PASSED)).append(">");
		
		// headers
		for (String s : testStepColumns) {
			step.sbDescription.append("<th>").append(s).append("</th>");
		}
		
		step.sbDescription.append("</tr>");
		
		data.testSteps.add(step);
	}

	@Override
	public void newTestStep(RunnerLeaf runnerLeaf, TestStepInfo testStepInfo) {
		TestCaseData data = (TestCaseData) runnerLeaf.getAttribute("hpalmData");

		if (data == null || data.testSteps.isEmpty()) {
			return;
		}

		// log test step to description
		TestStepData step = data.testSteps.get(data.testSteps.size() - 1);
		StringBuilder sb = step.sbDescription;

		for (Attachment attachment : testStepInfo.getAttachments()) {
			step.attachments.add(attachment);
		}

		sb.append("<tr ").append(cellStyle.get(testStepInfo.getTestStatus())).append(">");
		
		// Started
		String value = "";
		DateTime dt = testStepInfo.getStartingTime();
		if (dt != null && data.startTime != null) {
			value = new Duration(data.startTime, dt).getStandardSeconds() + "s";
		}
		sb.append("<td>").append(value).append("</td>");
		
		// Command
		sb.append("<td>").append(nullAsEmpty(testStepInfo.getCommand())).append("</td>");
		
		// Element Type
		sb.append("<td>").append(nullAsEmpty(getSingleStringArgument(testStepInfo, ElementType.class))).append("</td>");

		// Element Name
		sb.append("<td>").append(nullAsEmpty(getSingleStringArgument(testStepInfo, ElementName.class))).append("</td>");

		// Data
		sb.append("<td>").append(getArgumentsString(testStepInfo, null)).append("</td>");

		// Error Message
		sb.append("<td>").append(nullAsEmpty(testStepInfo.getErrorMessage())).append("</td>");

		// Technical Locator
		sb.append("<td>").append(nullAsEmpty(getSingleStringArgument(testStepInfo, TechnicalLocator.class))).append("</td>");

		// Technical Arguments
		sb.append("<td>").append(getArgumentsString(testStepInfo, TechnicalArgument.class)).append("</td>");

		// Comment
		sb.append("<td>");
		// convert stack trace into comment
		if (testStepInfo.getError() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			testStepInfo.getError().printStackTrace(pw);
			pw.flush();
			sb.append(HTMLUtil.escape(sw.toString()).replace("\n", "<br />"));
		}
		sb.append("</td>");
		sb.append("</tr>");

		if (data.alreadyFailed) {
			return;
		}

		// switch last step to FAILED if failed
		RunStepBuilder lastStep = step.runStepBuilder;
		switch (testStepInfo.getTestStatus()) {
			case FAILED:
			case FAILEDACCESS:
			case FAILEDAUTOMATION:
			case FAILEDPERFORMANCE:
			case INCONCLUSIVE:
				lastStep.setStatus(RunStepStatus.FAILED);
				// do not update status of BLOCKED runs
				if (!RunStepStatus.BLOCKED.displayName().equals(data.testRunBuilder.create().getStringFieldValue("status"))) {
					data.testRunBuilder.setStatus(RunStepStatus.FAILED.displayName());
				}
				data.alreadyFailed = true;
				break;
			default:
				break;
		}
	}

	@Override
	public void finishedTestLeaf(RunnerLeaf runnerLeaf) {
		TestCaseData data = (TestCaseData) runnerLeaf.getAttribute("hpalmData");
		if (data == null || !workerThread.isAlive()) {
			return;
		}

		DateTime endTime = new DateTime();
		// @formatter:off
		data.testRunBuilder
			.setExecutionDateAndTime(data.startTime.toDate())
			.setDuration((endTime.getMillis() - data.startTime.getMillis()) / 1000)
			.setName(runnerLeaf.getName().substring(runnerLeaf.getName().lastIndexOf('.') + 1));
		// @formatter:on

		// finish HTML of all steps
		for (TestStepData step : data.testSteps) {
			if (!step.sbDescription.toString().endsWith("</html>")) {
				step.sbDescription.append("</table></body></html>");
			}
		}

		workerThread.addTestRun(data);
	}

	@Override
	public void finishedTestProcess(RunnerTree runnerTree) {
		if (workerThread != null) {
			workerThread.terminate();

			// wait for worker thread to finish
			try {
				workerThread.join();
			}
			catch (InterruptedException e) {
				return;
			}
		}
	}

	private String getSingleStringArgument(TestStepInfo testStepInfo, Class<? extends Annotation> annotClass) {
		Object[] args = testStepInfo.getArguments(annotClass);
		if (args == null || args.length == 0) {
			return null;
		}
		return toString(args[0]);
	}

	private String getArgumentsString(TestStepInfo testStepInfo, Class<? extends Annotation> annotClass) {
		Object[] args = testStepInfo.getArguments(annotClass);
		if (args == null || args.length == 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();

		for (Object o : args) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(toString(o));
		}

		return sb.toString();
	}

	private String toString(Object o) {
		if (o == null) {
			return "null";
		}

		if (o.getClass().isArray()) {
			if (!o.getClass().getComponentType().isPrimitive()) {
				return Arrays.toString((Object[]) o);
			}
			else if (o.getClass().getComponentType() == int.class) {
				return Arrays.toString((int[]) o);
			}
			else if (o.getClass().getComponentType() == float.class) {
				return Arrays.toString((float[]) o);
			}
			else if (o.getClass().getComponentType() == boolean.class) {
				return Arrays.toString((boolean[]) o);
			}
			else if (o.getClass().getComponentType() == double.class) {
				return Arrays.toString((double[]) o);
			}
			else if (o.getClass().getComponentType() == short.class) {
				return Arrays.toString((short[]) o);
			}
		}

		return o.toString();
	}

	private static String nullAsEmpty(String s) {
		return s == null ? "" : s;
	}

	private static class TestCaseData {

		private TestRunBuilder testRunBuilder;

		private DateTime startTime;

		private long hpAlmId;

		private Long hpAlmConfigId;

		private List<TestStepData> testSteps = new ArrayList<TestStepData>();

		boolean alreadyFailed = false;

	}

	private static class TestStepData {

		private RunStepBuilder runStepBuilder;

		private List<Attachment> attachments = new ArrayList<Attachment>();

		private StringBuilder sbDescription;

	}

	private static class HpAlmWorkerThread extends Thread {

		private boolean terminated;

		private volatile boolean timeZoneInitialized;

		private HpAlmConfiguration configuration;

		private List<TestCaseData> buffer = new LinkedList<TestCaseData>();

		public HpAlmWorkerThread(HpAlmConfiguration configuration) {
			this.configuration = configuration;
		}

		private synchronized void terminate() {
			terminated = true;
			notify();
		}

		private synchronized boolean isTerminated() {
			return terminated;
		}

		private synchronized void addTestRun(TestCaseData data) {
			buffer.add(data);
			notify();
		}

		private synchronized boolean isBufferEmpty() {
			return buffer.isEmpty();
		}

		public synchronized boolean isTimeZoneInitialized() {
			return timeZoneInitialized;
		}

		@Override
		public void run() {
			// ensure configuration is valid
			try {
				configuration.getHpAlmUrl();
				configuration.getDomain();
				configuration.getProject();
				configuration.getUserName();
				configuration.getPassword();
				configuration.getTestSetFolderPath();
				configuration.getTestSetName();
			}
			catch (ConfigurationException ce) {
				LOG.error("HP ALM Connector configuration is invalid", ce);
				return;
			}

			HpAlmSession session;
			try {
				session = HpAlmSession.create(configuration.getHpAlmUrl(), configuration.getDomain(),
						configuration.getProject(), configuration.getUserName(), configuration.getPassword());
			}
			catch (IOException e) {
				LOG.error("Could not connect to HP ALM", e);
				return;
			}
			catch (HpAlmException e) {
				LOG.error("Could not connect to HP ALM", e);
				return;
			}

			// determine server time zone
			try {
				AbstractEntityBuilder.setTimeZone(session.determineServerTimeZone());
				synchronized (this) {
					timeZoneInitialized = true;
					notifyAll();
				}
			}
			catch (HpAlmException e) {
				LOG.warn("Could not determine server time zone", e);
			}
			catch (IOException e) {
				LOG.warn("Could not determine server time zone", e);
			}

			// ensure that configured path exists
			Entity testSetFolder;
			try {
				testSetFolder = HpAlmUtil.createTestSetFolderPath(session, configuration.getTestSetFolderPath());
			}
			catch (IOException e) {
				LOG.error("Could not create or retrieve configured test set folder " + configuration.getTestSetFolderPath(), e);
				return;
			}
			catch (HpAlmException e) {
				LOG.error("Could not create or retrieve configured test set folder " + configuration.getTestSetFolderPath(), e);
				return;
			}

			// create or get test set in folder
			Entity testSet;
			try {
				testSet = HpAlmUtil.createOrGetTestSet(session, testSetFolder.getId(),
						configuration.getTestSetName());
			}
			catch (IOException e) {
				LOG.error("Could not create or retrieve configured test set " + configuration.getTestSetName(), e);
				return;
			}
			catch (HpAlmException e) {
				LOG.error("Could not create or retrieve configured test set " + configuration.getTestSetName(), e);
				return;
			}
			long testSetId = testSet.getId();

			while (!isTerminated()) {
				// wait for buffer to receive an element
				while (!isTerminated() && isBufferEmpty()) {
					try {
						// wait up to one minute
						synchronized (this) {
							wait(60000);
						}
					}
					catch (InterruptedException e) {
						return;
					}
					// send keep-alive
					try {
						session.extendTimeout();
					}
					catch (Exception e) {
						LOG.warn("Lost connection to HP ALM; trying to reconnect", e);
						try {
							session.logout();
						}
						catch (Exception ee) { // NOPMD
							// ignore
						}

						try {
							session = HpAlmSession.create(configuration.getHpAlmUrl(), configuration.getDomain(),
									configuration.getProject(), configuration.getUserName(), configuration.getPassword());
						}
						catch (IOException ee) {
							LOG.error("Could not connect to HP ALM", ee);
							return;
						}
						catch (HpAlmException ee) {
							LOG.error("Could not connect to HP ALM", ee);
							return;
						}
					}
				}

				while (!isBufferEmpty()) {
					TestCaseData data;
					synchronized (this) {
						data = buffer.remove(0);
					}

					try {
						writeTestRun(session, data, testSetId, data.hpAlmId, data.hpAlmConfigId);
					}
					catch (IOException e) {
						LOG.error("Could not write test case "
								+ (data != null ? (data.hpAlmId + "/" + data.hpAlmConfigId) : "(unknown)")
								+ " to HP ALM", e);
					}
					catch (HpAlmException e) {
						LOG.error("Could not write test case "
								+ (data != null ? (data.hpAlmId + "/" + data.hpAlmConfigId) : "(unknown)") + " to HP ALM", e);
					}
				}
			}

			try {
				session.logout();
			}
			catch (Exception e) {
				LOG.warn("Exception when logging out from HP ALM", e);
			}
		}

		private void writeTestRun(HpAlmSession session, TestCaseData data, long testSetId, long testId, Long testConfigId)
				throws IOException,
				HpAlmException {
			// create or get the Test Instance
			Entity testInstance = HpAlmUtil.createOrGetTestInstance(session, testSetId, testId, testConfigId);

			if (testInstance == null) {
				LOG.error("Test instance for testId / testConfigId " + testId + "/" + testConfigId + " could not be created.");
				return;
			}

			// create the Test Run
			Entity testRun = data.testRunBuilder.setTestSetId(testSetId).setTestInstanceId(testInstance.getId())
					.setOwner(configuration.getUserName()).create();
			testRun = session.createEntity(testRun);

			// delete auto-created steps (from test plan)
			// first, collect them because of HP ALM pagination (otherwise, second page would be empty)
			List<Entity> toDelete = new ArrayList<Entity>();
			EntityCollection ec = session.queryEntities("run-step", "parent-id[" + testRun.getId() + "]");
			for (Entity e : ec) {
				if (e.getId() > 0) {
					toDelete.add(e);
				}
			}

			for (Entity e : toDelete) {
				session.deleteEntity(e);
			}

			// create the Test Steps
			Set<String> addedStepNames = new HashSet<String>();
			long testRunId = testRun.getId();
			for (TestStepData step : data.testSteps) {
				RunStepBuilder stepBuilder = step.runStepBuilder.setTestRunId(testRunId);
				if (configuration.isWriteDescriptionAndAttachments()) {
					stepBuilder.setDescription(step.sbDescription.toString());
				}

				Entity stepEntity = stepBuilder.create();
				stepEntity = session.createEntity(stepEntity);

				// attach attachments
				if (configuration.isWriteDescriptionAndAttachments()) {
					int attachmentCount = 0;
					for (Attachment attachment : step.attachments) {
						String fileName = "attachment_" + (++attachmentCount) + "." + attachment.getFileExtension();
						session.createAttachment(stepEntity, fileName, new ByteArrayInputStream(attachment.getFileData()));
					}
				}

				addedStepNames.add(stepEntity.getStringFieldValue("name"));
			}

			// update Test Instance status
			Entity testInstanceUpdate = new TestInstanceBuilder().setStatus(testRun.getStringFieldValue("status"))
					.setExecDateTimeFromEntity(testRun).create();
			session.updateEntity(testInstance.getId(), testInstanceUpdate);

			// delete auto-generated Fast_Run
			ec = session.queryEntities("run", "id[>" + testRunId + "]; test-id[" + data.hpAlmId + "]; cycle-id[" + testSetId
					+ "]; name['Fast_Run_*']");
			if (ec.getTotalCount() > 0) {
				for (Entity e : ec) {
					session.deleteEntity(e);
				}
			}
		}

	}

}
