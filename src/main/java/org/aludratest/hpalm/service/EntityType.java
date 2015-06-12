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
package org.aludratest.hpalm.service;

public enum EntityType {

	DEFECT("defect"), DEFECT_LINK("defect_link"), RELEASE("release"), RELEASE_CYCLE("release-cycle"), RELEASE_FOLDER(
			"release-folder"), REQUIREMENT("requirement"), RESOURCE("resource"), RUN("run"), TEST("test"), TEST_CONFIG(
			"test-config"), TEST_RUN("test-run"), TEST_SET("test-set"), TEST_FOLDER("test-folder"), TEST_SET_FOLDER(
			"test-set-folder");

	private String hpAlmTypeName;

	private EntityType(String hpAlmTypeName) {
		this.hpAlmTypeName = hpAlmTypeName;
	}

	public String getHpAlmTypeName() {
		return hpAlmTypeName;
	}

}
