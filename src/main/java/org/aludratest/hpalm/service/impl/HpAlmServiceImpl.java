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
package org.aludratest.hpalm.service.impl;

import java.io.IOException;

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.config.Preferences;
import org.aludratest.exception.AccessFailure;
import org.aludratest.hpalm.impl.HpAlmConfiguration;
import org.aludratest.hpalm.infrastructure.HpAlmException;
import org.aludratest.hpalm.infrastructure.HpAlmSession;
import org.aludratest.hpalm.service.HpAlmCondition;
import org.aludratest.hpalm.service.HpAlmInteraction;
import org.aludratest.hpalm.service.HpAlmService;
import org.aludratest.hpalm.service.HpAlmVerification;
import org.aludratest.service.AbstractConfigurableAludraService;
import org.codehaus.plexus.component.annotations.Requirement;

@ConfigProperties({
	@ConfigProperty(name = "hpalmUrl", description = "The URL to HP ALM. Must end with /qcbin, WITHOUT a terminating slash.", required = true, type = String.class),
	@ConfigProperty(name = "userName", description = "User name for HP ALM connections.", type = String.class, required = true),
	@ConfigProperty(name = "password", description = "Password for HP ALM user.", type = String.class, required = true),
		@ConfigProperty(name = "domain", description = "HP ALM Domain.", defaultValue = "DEFAULT", type = String.class, required = true),
		@ConfigProperty(name = "project", description = "HP ALM Project to use.", type = String.class, required = true) })
public class HpAlmServiceImpl extends AbstractConfigurableAludraService implements HpAlmService {

	@Requirement
	private HpAlmConfiguration configuration;

	private HpAlmSession session;

	private HpAlmInteraction interaction;

	private HpAlmVerificationCheckImpl verificationCondition;

	@Override
	public String getPropertiesBaseName() {
		return "hpalm";
	}

	@Override
	public void configure(Preferences preferences) {
		// do NOT configure configuration here, because it should already be configured by Plexus lifecycle
		configuration.configure(preferences);
	}

	@Override
	public String getDescription() {
		return "Provides methods to interact with an HP ALM instance.";
	}

	@Override
	public HpAlmInteraction perform() {
		return interaction;
	}

	@Override
	public HpAlmVerification verify() {
		return verificationCondition;
	}

	@Override
	public HpAlmCondition check() {
		return verificationCondition;
	}

	@Override
	public void close() {
		try {
			session.logout();
		}
		catch (Throwable t) {
			// ignore
		}
	}

	@Override
	public void initService() {
		// connect to HP ALM
		try {
			session = HpAlmSession.create(configuration.getHpAlmUrl(), configuration.getDomain(), configuration.getProject(),
					configuration.getUserName(), configuration.getPassword());

			interaction = new HpAlmInteractionImpl(session);
			verificationCondition = new HpAlmVerificationCheckImpl(session);
		}
		catch (IOException e) {
			throw new AccessFailure("Could not connect to HP ALM", e);
		}
		catch (HpAlmException e) {
			throw new AccessFailure("Could not login to HP ALM", e);
		}
	}

}
