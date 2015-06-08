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

import org.aludratest.config.MutablePreferences;
import org.aludratest.config.Preferences;
import org.aludratest.config.ValidatingPreferencesWrapper;

/** Default implementation of the Configuration service for the HP ALM Connector.
 * 
 * @author falbrech */
public class HpAlmConfigurationImpl implements HpAlmConfiguration {

	private ValidatingPreferencesWrapper preferences;

	@Override
	public String getPropertiesBaseName() {
		return "hpalm";
	}

	@Override
	public void fillDefaults(MutablePreferences preferences) {
		// everything is in Annotations in base class
	}

	@Override
	public void configure(Preferences preferences) {
		this.preferences = new ValidatingPreferencesWrapper(preferences);
	}

	@Override
	public boolean isEnabled() {
		return preferences.getBooleanValue("enabled", true);
	}

	@Override
	public String getHpAlmUrl() {
		return preferences.getRequiredStringValue("hpalmUrl");
	}

	@Override
	public String getUserName() {
		return preferences.getRequiredStringValue("userName");
	}

	@Override
	public String getPassword() {
		return preferences.getRequiredStringValue("password");
	}

	@Override
	public String getDomain() {
		return preferences.getRequiredStringValue("domain");
	}

	@Override
	public String getProject() {
		return preferences.getRequiredStringValue("project");
	}

	@Override
	public String getTestSetFolderPath() {
		return preferences.getRequiredStringValue("testSetFolderPath");
	}

	@Override
	public String getTestSetName() {
		return preferences.getRequiredStringValue("testSetName");
	}

	@Override
	public boolean isWriteDescriptionAndAttachments() {
		return preferences.getBooleanValue("writeDescriptionAndAttachments", true);
	}

}
