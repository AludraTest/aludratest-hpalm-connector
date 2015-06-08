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
