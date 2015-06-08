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
package org.aludratest.hpalm;

import org.aludratest.scheduler.node.RunnerLeaf;

/** Interface for services being able to retrieve the HP ALM test ID for a given AludraTest test case. The HP ALM connector needs
 * such a service to be present and being able to return an HP ALM test ID for running test cases. If the service is not able to
 * return an HP ALM test ID for a given test case, the test case run is not logged to HP ALM. <br>
 * The AludraTest HP ALM connector ships with a default implementation; have a look at
 * {@link org.aludratest.hpalm.impl.DefaultTestCaseIdResolver}. If all of your test classes obey to the expected pattern of this
 * default implementation, there is no need to implement an own resolver. Otherwise, you will have to implement this interface and
 * register your implementation via the <code>aludraservice.properties</code> configuration file.
 * 
 * @author falbrech */
public interface TestCaseIdResolver {

	public final static String ROLE = TestCaseIdResolver.class.getName();

	/** Returns the HP ALM test ID to assign the given runner leaf to. A return value of <code>null</code> indicates that such an
	 * ID could not be determined. In this case, a warning is logged, and the test case execution is not logged to HP ALM.
	 * 
	 * @param testCase Runner leaf representing the test case execution.
	 * 
	 * @return The HP ALM test ID to assign the test case execution to, or <code>null</code> if no ID could be determined. */
	public Long getHpAlmTestId(RunnerLeaf testCase);

	/** Returns the HP ALM Configuration ID to use for the given runner leaf. Unlike {@link #getHpAlmTestId(RunnerLeaf)}, a
	 * <code>null</code> return value does not skip the logging for this leaf, but assigns the run result to the whole test
	 * instead of a test configuration.
	 * 
	 * @param testCase Runner leaf representing the test case execution.
	 * 
	 * @return The HP ALM configuration ID to assign the given runner leaf to, or <code>null</code> to assign it to the test. */
	public Long getHpAlmTestConfigId(RunnerLeaf testCase);

}
