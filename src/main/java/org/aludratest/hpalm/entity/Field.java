package org.aludratest.hpalm.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "value" })
public class Field {

	@XmlElement(name = "Value", required = true)
	protected List<String> value;
	@XmlAttribute(name = "Name", required = true)
	protected String name;

	public Field() {
	}

	public Field(Field field) {
		name = field.name;
		value = field.value == null ? null : new ArrayList<String>(field.value);
	}

	/** Gets the value of the value property.
	 * 
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore, any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is no set method for the value
	 * property.
	 * 
	 * For example, to add a new item, do as follows:
	 * 
	 * getValue().add(newItem);
	 * 
	 * 
	 * Objects of the following type(s) are allowed in the list {@link String } */
	public List<String> getValue() {
		if (value == null) {
			value = new ArrayList<String>();
		}
		return this.value;
	}

	/** Gets the value of the name property.
	 * 
	 * @return possible object is {@link String } */
	public String getName() {
		return name;
	}

	/** Sets the value of the name property.
	 * 
	 * @param value allowed object is {@link String } */
	public void setName(String value) {
		this.name = value;
	}

}