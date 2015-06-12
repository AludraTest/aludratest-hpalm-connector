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

	public ServerTime getServerTime();

	public EntityCollection getAllEntities(@ElementType EntityType entityType);

	public Entity getSingleEntity(@ElementType EntityType entityType, @ElementName long id);

	public void updateEntity(@ElementType EntityType entityType, @ElementName long id, Entity value);

	public void deleteEntity(@ElementType EntityType entityType, @ElementName long id);

	public EntityCollection queryEntities(@ElementType EntityType entityType, @TechnicalLocator String query);

}