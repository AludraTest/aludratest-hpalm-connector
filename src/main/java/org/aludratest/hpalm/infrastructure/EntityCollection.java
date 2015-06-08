package org.aludratest.hpalm.infrastructure;

import org.aludratest.hpalm.entity.Entity;

/** An interface to query a collection of entities returned by HP ALM. This interface hides the internal data paging of the HP ALM
 * REST API, so clients can treat the collection as one huge set. All navigational issues on the set are performed automatically
 * by the implementation. <br>
 * Clients must <b>not</b> implement this interface! It is returned from operations on {@link HpAlmSession}.
 * 
 * @author falbrech */
public interface EntityCollection extends Iterable<Entity> {
	
	/** Returns the total count of elements in this collection, as returned from HP ALM.
	 * 
	 * @return The total count of elements in this collection. 0 for an empty collection. */
	public int getTotalCount();

}
