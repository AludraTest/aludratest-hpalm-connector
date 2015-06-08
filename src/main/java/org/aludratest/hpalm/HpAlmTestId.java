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
