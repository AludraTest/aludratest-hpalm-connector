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
package org.aludratest.hpalm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation for AludraTest test classes (extending AludraTestCase) to indicate which HP ALM test ID is associated with the test
 * class. The default implementation of the {@link TestCaseIdResolver} service first tries to lookup such an annotation to
 * determine the HP ALM test ID.
 * 
 * @author falbrech */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HpAlmTestId {

	public long value();

}
