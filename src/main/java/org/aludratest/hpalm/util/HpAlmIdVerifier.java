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
package org.aludratest.hpalm.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.aludratest.AludraTest;
import org.aludratest.hpalm.TestCaseIdResolver;
import org.aludratest.hpalm.entity.Entity;
import org.aludratest.hpalm.impl.HpAlmConfiguration;
import org.aludratest.hpalm.infrastructure.EntityCollection;
import org.aludratest.hpalm.infrastructure.HpAlmException;
import org.aludratest.hpalm.infrastructure.HpAlmSession;
import org.aludratest.hpalm.infrastructure.HpAlmUtil;
import org.aludratest.scheduler.RunnerTree;
import org.aludratest.scheduler.RunnerTreeBuilder;
import org.aludratest.scheduler.node.RunnerGroup;
import org.aludratest.scheduler.node.RunnerLeaf;
import org.aludratest.scheduler.node.RunnerNode;
import org.aludratest.service.AludraServiceManager;

/** Utility program to analyze the test and test configuration IDs of an integration test suite (using AludraTest). For each test
 * ID found, it is checked if this test ID also exists in HP ALM. If it does, all associated test configuration IDs of HP ALM must
 * also exist locally as test configuration. And all local test configurations must have a matching test configuration in HP ALM.
 * Every deviation is printed out to STDERR. <br>
 * <br>
 * To use this program, start it from within the classpath of your project, and add the fully qualified name of the test suite to
 * analyze as the one and only program argument.
 * 
 * @author falbrech */
public class HpAlmIdVerifier {

	private HpAlmSession session;

	private TestCaseIdResolver idResolver;

	private Set<Long> checkedTests = new HashSet<Long>();

	private HpAlmIdVerifier(HpAlmSession session, TestCaseIdResolver idResolver) {
		this.session = session;
		this.idResolver = idResolver;
	}

	public static void main(String[] args) throws Exception {
		// only one argument allowed, and this must be valid AludraTest class
		if (args.length != 1) {
			printUsage("Exactly one argument required.");
			System.exit(3);
			return;
		}

		String className = args[0];
		Class<?> testClass;
		try {
			testClass = Class.forName(className);
		}
		catch (Exception e) {
			printUsage("Could not lookup test class " + className + " on classpath.");
			System.exit(1);
			return;
		}

		AludraTest framework = AludraTest.startFramework();
		try {
			AludraServiceManager serviceManager = framework.getServiceManager();
			RunnerTreeBuilder builder = serviceManager.newImplementorInstance(RunnerTreeBuilder.class);
			System.out.println("Building execution tree for analysis, this can take a while...");
			RunnerTree runnerTree = builder.buildRunnerTree(testClass);

			HpAlmConfiguration configuration = serviceManager.newImplementorInstance(HpAlmConfiguration.class);
			TestCaseIdResolver idResolver = serviceManager.newImplementorInstance(TestCaseIdResolver.class);

			// ensure configuration is valid
			configuration.getHpAlmUrl();
			configuration.getDomain();
			configuration.getProject();
			configuration.getUserName();
			configuration.getPassword();

			HpAlmSession session = HpAlmSession.create(configuration.getHpAlmUrl(), configuration.getDomain(),
					configuration.getProject(), configuration.getUserName(), configuration.getPassword());

			HpAlmIdVerifier verifier = new HpAlmIdVerifier(session, idResolver);
			try {
				verifier.verify(runnerTree.getRoot());
			}
			finally {
				try {
					session.logout();
				}
				catch (Exception e) {
					// ignore
				}
			}
		}
		finally {
			framework.stopFramework();
		}
	}

	private void verify(RunnerGroup group) throws IOException, HpAlmException {
		// dive into children
		for (RunnerNode node : group.getChildren()) {
			if (node instanceof RunnerGroup) {
				verify((RunnerGroup) node);
			}
			else if (node instanceof RunnerLeaf) {
				verify((RunnerLeaf) node);
			}
		}
	}

	private void verify(RunnerLeaf leaf) throws IOException, HpAlmException {
		Long testId = idResolver.getHpAlmTestId(leaf);
		if (testId != null) {
			// verify that test exists
			try {
				session.getTest(testId.longValue());
			}
			catch (HpAlmException e) {
				System.err.println("Test ID not found in HP ALM: " + HpAlmUtil.DF_ID.format(testId));
				return;
			}
			
			// get all existing HP ALM config IDs, if not yet tested
			if (!checkedTests.contains(testId)) {
				checkedTests.add(testId);
				// get all configurations for that test
				EntityCollection configs = session.queryEntities("test-config", "parent-id[" + HpAlmUtil.DF_ID.format(testId)
						+ "]");
				verifyHpAlmConfigsExistLocal(testId, leaf.getParent(), configs);
			}

			// verify ID exists in HP ALM
			Long configId = idResolver.getHpAlmTestConfigId(leaf);
			if (configId != null) {
				EntityCollection configs = session.queryEntities("test-config", "id[" + HpAlmUtil.DF_ID.format(configId)
						+ "]; parent-id[" + HpAlmUtil.DF_ID.format(testId) + "]");
				if (configs.getTotalCount() == 0) {
					System.err.println("Local test configuration does not exist in HP ALM: " + HpAlmUtil.DF_ID.format(testId)
							+ "/" + HpAlmUtil.DF_ID.format(configId));
				}
			}
			else {
				System.err.println("Local test configuration ID could not be determined for: " + leaf.getName());
			}
		}
	}

	private void verifyHpAlmConfigsExistLocal(Long testId, RunnerGroup group, EntityCollection configs) {
		Set<Long> notFoundIds = new HashSet<Long>();
		for (Entity config : configs) {
			notFoundIds.add(Long.valueOf(config.getId()));
		}

		for (RunnerNode node : group.getChildren()) {
			if (node instanceof RunnerLeaf) {
				notFoundIds.remove(idResolver.getHpAlmTestConfigId((RunnerLeaf) node));
			}
		}

		for (Long id : notFoundIds) {
			System.err.println("HP ALM Test configuration ID has no local test configuration data: "
					+ HpAlmUtil.DF_ID.format(testId) + "/" + HpAlmUtil.DF_ID.format(id));
		}
	}

	private static void printUsage(String errorMessage) {
		if (errorMessage != null) {
			System.err.println("ERROR: " + errorMessage);
			System.out.println();
		}

		System.out.println("HpAlmIdVerifier utility");
		System.out.println("=======================");
		System.out.println("Verifies for a given test or test suite class that all determined HP ALM IDs are valid.");
		System.out.println("Found configuration IDs must match their test IDs.");
		System.out.println("Non-matching or non-existing IDs are reported.");
		System.out.println();
		System.out.println("Usage:");
		System.out.println("java " + HpAlmIdVerifier.class.getName() + " <AludraTestSuiteClass>");
		System.out.println();
		System.out.println("AludraTestSuiteClass must be the fully qualified name of a class extending AludraTestCase");
		System.out.println("or being annotated with @Suite.");
		System.out.println("hpalm.properties must exist on classpath, according to AludraTest configuration guide.");
	}

}
