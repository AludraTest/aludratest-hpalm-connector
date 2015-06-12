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
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.exception.TechnicalException;
import org.aludratest.hpalm.entity.Entity;
import org.aludratest.hpalm.infrastructure.EntityCollection;
import org.aludratest.hpalm.infrastructure.HpAlmException;
import org.aludratest.hpalm.infrastructure.HpAlmSession;
import org.aludratest.hpalm.infrastructure.HpAlmUtil;
import org.aludratest.hpalm.service.EntityType;
import org.aludratest.hpalm.service.HpAlmCondition;
import org.aludratest.hpalm.service.HpAlmVerification;
import org.aludratest.service.SystemConnector;
import org.aludratest.testcase.event.attachment.Attachment;
import org.databene.commons.Validator;

public class HpAlmVerificationCheckImpl implements HpAlmVerification, HpAlmCondition {

	private HpAlmSession session;

	public HpAlmVerificationCheckImpl(HpAlmSession session) {
		this.session = session;
	}

	@Override
	public void assertEntityFieldMatches(EntityType entityType, long id, String fieldName, Validator<String> validator) {
		try {
			// always query to avoid direct HP ALM exception
			EntityCollection ec = session.queryEntities(entityType.getHpAlmTypeName(), "id[" + HpAlmUtil.DF_ID.format(id) + "]");
			if (ec.getTotalCount() == 0) {
				throw new FunctionalFailure("Entity does not exist");
			}

			Entity e = ec.iterator().next();
			String value = e.getStringFieldValue(fieldName);

			if (!validator.valid(value)) {
				throw new FunctionalFailure("Value '" + value + " ' does not match Validator " + validator);
			}
		}
		catch (IOException e) {
			throw new AccessFailure("Could not query " + entityType.getHpAlmTypeName() + " #" + id, e);
		}
		catch (HpAlmException e) {
			throw new TechnicalException("Could not query " + entityType.getHpAlmTypeName() + " #" + id, e);
		}
	}

	@Override
	public void assertEntityExists(EntityType entityType, long id) {
		if (!entityExists(entityType, id)) {
			throw new FunctionalFailure("Entity does not exist");
		}
	}

	@Override
	public void assertEntityNotExists(EntityType entityType, long id) {
		if (entityExists(entityType, id)) {
			throw new FunctionalFailure("Entity exists, but expected non-existence");
		}
	}

	@Override
	public void assertAnyEntityExists(EntityType entityType, String query) {
		if (!anyEntityExists(entityType, query)) {
			throw new FunctionalFailure("No " + entityType.getHpAlmTypeName() + " matches query '" + query + "'");
		}
	}

	@Override
	public void assertNoEntityExists(EntityType entityType, String query) {
		if (anyEntityExists(entityType, query)) {
			throw new FunctionalFailure("At least one " + entityType.getHpAlmTypeName() + " matches query '" + query
					+ "', which is unexpected");
		}
	}

	@Override
	public boolean anyEntityExists(EntityType entityType, String query) {
		try {
			// always query to avoid direct HP ALM exception
			EntityCollection ec = session.queryEntities(entityType.getHpAlmTypeName(), query);
			return ec.getTotalCount() > 0;
		}
		catch (IOException e) {
			throw new AccessFailure("Could not query " + entityType.getHpAlmTypeName(), e);
		}
		catch (HpAlmException e) {
			throw new TechnicalException("Could not query " + entityType.getHpAlmTypeName(), e);
		}
	}

	@Override
	public boolean entityExists(EntityType entityType, long id) {
		try {
			// always query to avoid direct HP ALM exception
			EntityCollection ec = session.queryEntities(entityType.getHpAlmTypeName(), "id[" + HpAlmUtil.DF_ID.format(id) + "]");
			return ec.getTotalCount() > 0;
		}
		catch (IOException e) {
			throw new AccessFailure("Could not query " + entityType.getHpAlmTypeName() + " #" + id, e);
		}
		catch (HpAlmException e) {
			throw new TechnicalException("Could not query " + entityType.getHpAlmTypeName() + " #" + id, e);
		}
	}

	@Override
	public List<Attachment> createDebugAttachments() {
		return null;
	}

	@Override
	public List<Attachment> createAttachments(Object object, String title) {
		return null;
	}

	@Override
	public void setSystemConnector(SystemConnector systemConnector) {
	}

}
