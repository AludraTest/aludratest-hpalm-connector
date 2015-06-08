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
package org.aludratest.hpalm.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aludratest.hpalm.HpAlmTestId;
import org.aludratest.hpalm.TestCaseIdResolver;
import org.aludratest.scheduler.node.RunnerLeaf;

/** Default implementation of the {@link TestCaseIdResolver} service interface. This default implementation tries to determine the
 * HP ALM test ID for a given test case with the following algorithm:
 * <ol>
 * <li>First, it tries to load a Java class with the name of the parent RunnerGroup of the given RunnerLeaf.</li>
 * <li>If such a class could be loaded, it is scanned for an Annotation of type {@link HpAlmTestId}.</li>
 * <li>If such an annotation is present, its value is taken as HP ALM test ID.</li>
 * <li>If this way was not successful, the name of the parent RunnerGroup is scanned for the pattern <code>ID_nnnnnn_</code>,
 * where <code>nnnnnn</code> must be numerical and consist of one to six digits. If such a pattern is found, the numerical value
 * is returned.</li>
 * <li>If all these steps were unsuccessful, <code>null</code> is returned, causing the test case not to be logged to HP ALM.</li>
 * </ol>
 * 
 * @author falbrech */
public class DefaultTestCaseIdResolver implements TestCaseIdResolver {

	private static final Pattern PATTERN_ID = Pattern.compile("(^|[^C])ID_([0-9]{1,6})_");

	private static final Pattern PATTERN_CONFIG_ID = Pattern.compile("CID_([0-9]{1,6})_");

	@Override
	public Long getHpAlmTestId(RunnerLeaf testCase) {
		String testClassName;

		if (testCase.getTestInvoker() != null && testCase.getTestInvoker().getTestClass() != null) {
			Class<?> testClass = testCase.getTestInvoker().getTestClass();

			HpAlmTestId testId = testClass.getAnnotation(HpAlmTestId.class);
			if (testId != null) {
				return testId.value();
			}

			testClassName = testClass.getName();
		}
		else {
			testClassName = testCase.getParent().getName();
		}

		// try to find pattern
		Matcher m = PATTERN_ID.matcher(testClassName);
		if (m.find()) {
			return Long.valueOf(m.group(2));
		}

		return null;
	}

	@Override
	public Long getHpAlmTestConfigId(RunnerLeaf testCase) {
		// try to find pattern
		Matcher m = PATTERN_CONFIG_ID.matcher(testCase.getName());
		if (m.find()) {
			return Long.valueOf(m.group(1));
		}

		return null;
	}

}
