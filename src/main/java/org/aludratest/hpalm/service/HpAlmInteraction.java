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

import org.aludratest.hpalm.entity.Entity;
import org.aludratest.hpalm.infrastructure.EntityCollection;
import org.aludratest.hpalm.infrastructure.ServerTime;
import org.aludratest.impl.log4testing.ElementName;
import org.aludratest.impl.log4testing.ElementType;
import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.service.Interaction;

public interface HpAlmInteraction extends Interaction {

	/** Returns the server time of the HP ALM server.
	 * 
	 * @return The server time of the HP ALM server. */
	public ServerTime getServerTime();

	/** Returns a collection with all entities of a given entity type.
	 * 
	 * @param entityType Entity type to retrieve the entities from.
	 * 
	 * @return Collection with all entities of this type. This collection may or may not be paging, i.e. after N elements, a new
	 *         query to the HP ALM server could automatically be issued to retrieve next N elements. */
	public EntityCollection getAllEntities(@ElementType EntityType entityType);

	/** Returns a single entity of the given type with the given ID.
	 * 
	 * @param entityType Type of the entity.
	 * @param id ID of the entity.
	 * 
	 * @return The entity. */
	public Entity getSingleEntity(@ElementType EntityType entityType, @ElementName long id);

	/** Creates a new entity of the given type. The entity object must have all desired (and required) values set, but must not
	 * have an ID set. The method returns the newly created entity object, which can e.g. be queried for the generated ID.
	 * 
	 * @param entityType Type of the entity to create.
	 * @param value Entity containing all required values. Do not use this object after a call to this method.
	 * 
	 * @return The created entity. Use this result for further operations on this entity. */
	public Entity createEntity(@ElementType EntityType entityType, Entity value);

	/** Updates the given entity.
	 * 
	 * @param entityType Type of the entity to update.
	 * @param id ID of the entity to update.
	 * @param value Entity object containing all fields for the entity. */
	public void updateEntity(@ElementType EntityType entityType, @ElementName long id, Entity value);

	/** Deletes the given entity.
	 * 
	 * @param entityType Type of the entity to delete.
	 * @param id ID of the entity to delete. */
	public void deleteEntity(@ElementType EntityType entityType, @ElementName long id);

	/** Executes a complex query in HP ALM for entities of a given type.
	 * @param entityType Type of the entities to query.
	 * @param query A complex HP ALM query. See HP ALM REST API documentation for query syntax.
	 * 
	 * @return Collection with all entities matching the query (maybe none). This collection may or may not be paging, i.e. after
	 *         N elements, a new query to the HP ALM server could automatically be issued to retrieve next N elements. */
	public EntityCollection queryEntities(@ElementType EntityType entityType, @TechnicalLocator String query);

}