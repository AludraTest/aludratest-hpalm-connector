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

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.config.Configurable;

/** Configuration service for the AludraTest HP ALM Connector. Use the provided configuration properties to configure the
 * connector.
 * 
 * @author falbrech */
@ConfigProperties({
		@ConfigProperty(name = "hpalmUrl", description = "The URL to HP ALM. Must end with /qcbin, WITHOUT a terminating slash.", required = true, type = String.class),
		@ConfigProperty(name = "userName", description = "User name for HP ALM connections.", type = String.class, required = true),
		@ConfigProperty(name = "password", description = "Password for HP ALM user.", type = String.class, required = true),
		@ConfigProperty(name = "domain", description = "HP ALM Domain.", defaultValue = "DEFAULT", type = String.class, required = true),
		@ConfigProperty(name = "project", description = "HP ALM Project to use.", type = String.class, required = true),
		@ConfigProperty(name = "testSetFolderPath", description = "HP ALM Folder to use for test set creation. Use forward slashes to separate folder names.", type = String.class, required = true, defaultValue = "Root/AludraTest"),
		@ConfigProperty(name = "testSetName", description = "Name of the test set to create within the test set folder for this run. If the test set does already exist, it is re-used.", type = String.class, required = true),
		@ConfigProperty(name = "writeDescriptionAndAttachments", description = "If set to true, a detailed HTML description and attachments (e.g. screenshots) will be added to written run steps. Otherwise, this data is not written, e.g. to reduce the amount of data written to HP ALM. If writeSteps is set to false, this setting is not used at all.", type = boolean.class, defaultValue = "true"),
		@ConfigProperty(name = "enabled", description = "If set to false, no test runs will be written to HP ALM.", defaultValue = "true", type = boolean.class, required = false),
		@ConfigProperty(name = "writeSteps", description = "If set to false, run steps will not be written to HP ALM, only test runs and their result status. This can be used to reduce number of lines written to HP ALM's steps table.", type = boolean.class, defaultValue = "true", required = false) })
public interface HpAlmConfiguration extends Configurable {

	public boolean isEnabled();

	public String getHpAlmUrl();

	public String getUserName();

	public String getPassword();

	public String getDomain();

	public String getProject();

	public String getTestSetFolderPath();

	public String getTestSetName();

	public boolean isWriteDescriptionAndAttachments();

	public boolean isWriteSteps();

}
