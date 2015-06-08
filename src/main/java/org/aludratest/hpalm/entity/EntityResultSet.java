package org.aludratest.hpalm.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Entities")
public class EntityResultSet {

	@XmlElement(name = "Entity")
	protected List<Entity> entities;

	@XmlAttribute(name = "TotalResults")
	protected int totalResults;

	public EntityResultSet(List<? extends Entity> entities) {
		this.entities = new ArrayList<Entity>(entities);
	}

	public EntityResultSet() {
	}

	public int getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	public List<Entity> getEntities() {
		return entities;
	}

}
