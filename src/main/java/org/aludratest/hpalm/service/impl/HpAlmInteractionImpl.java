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
import java.util.List;

import org.aludratest.exception.AccessFailure;
import org.aludratest.exception.AutomationException;
import org.aludratest.hpalm.entity.Entity;
import org.aludratest.hpalm.infrastructure.EntityCollection;
import org.aludratest.hpalm.infrastructure.HpAlmException;
import org.aludratest.hpalm.infrastructure.HpAlmSession;
import org.aludratest.hpalm.infrastructure.ServerTime;
import org.aludratest.hpalm.service.EntityType;
import org.aludratest.hpalm.service.HpAlmInteraction;
import org.aludratest.service.Interaction;
import org.aludratest.service.SystemConnector;
import org.aludratest.testcase.event.attachment.Attachment;

public class HpAlmInteractionImpl implements Interaction, HpAlmInteraction {

	private HpAlmSession session;

	public HpAlmInteractionImpl(HpAlmSession session) {
		this.session = session;
	}

	@Override
	public ServerTime getServerTime() {
		try {
			return session.getServerTime();
		}
		catch (IOException e) {
			throw new AccessFailure("Could not retrieve server time", e);
		}
		catch (HpAlmException e) {
			throw new AutomationException("Could not retrieve server time", e);
		}
	}

	@Override
	public EntityCollection getAllEntities(EntityType entityType) {
		try {
			return session.queryEntities(entityType.getHpAlmTypeName(), null);
		}
		catch (IOException e) {
			throw new AccessFailure("Could not query entity collection '" + entityType.getHpAlmTypeName() + "'", e);
		}
		catch (HpAlmException e) {
			throw new AutomationException("Could not query entity collection '" + entityType.getHpAlmTypeName() + "'", e);
		}
	}

	@Override
	public Entity getSingleEntity(EntityType entityType, long id) {
		try {
			return session.getEntity(entityType.getHpAlmTypeName(), id);
		}
		catch (IOException e) {
			throw new AccessFailure("Could not query " + entityType + " #" + id, e);
		}
		catch (HpAlmException e) {
			throw new AutomationException("Could not query " + entityType + " #" + id, e);
		}
	}

	@Override
	public void updateEntity(EntityType entityType, long id, Entity value) {
		try {
			session.updateEntity(id, value);
		}
		catch (IOException e) {
			throw new AccessFailure("Could not update " + entityType + " #" + id, e);
		}
		catch (HpAlmException e) {
			throw new AutomationException("Could not update " + entityType + " #" + id, e);
		}
	}

	@Override
	public void deleteEntity(EntityType entityType, long id) {
		try {
			session.deleteEntity(entityType.getHpAlmTypeName(), id);
		}
		catch (IOException e) {
			throw new AccessFailure("Could not delete " + entityType + " #" + id, e);
		}
		catch (HpAlmException e) {
			throw new AutomationException("Could not delete " + entityType + " #" + id, e);
		}
	}

	@Override
	public EntityCollection queryEntities(EntityType entityType, String query) {
		try {
			return session.queryEntities(entityType.getHpAlmTypeName(), query);
		}
		catch (IOException e) {
			throw new AccessFailure("Could not query entities of type " + entityType, e);
		}
		catch (HpAlmException e) {
			throw new AutomationException("Could not query entities of type " + entityType, e);
		}
	}

	@Override
	public List<Attachment> createDebugAttachments() {
		// no attachments supported
		return null;
	}

	@Override
	public List<Attachment> createAttachments(Object object, String title) {
		// no attachments supported
		return null;
	}

	@Override
	public void setSystemConnector(SystemConnector systemConnector) {
		// System connector not supported by HP ALM service
	}

}
