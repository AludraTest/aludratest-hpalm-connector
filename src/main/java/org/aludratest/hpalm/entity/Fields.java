package org.aludratest.hpalm.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "fieldList" })
@XmlRootElement(name = "Fields")
public class Fields {

	@XmlElement(name = "Field", required = true)
	protected List<Field> fieldList;

	public Fields(Fields fields) {
		fieldList = new ArrayList<Field>();

		if (fields.fieldList != null) {
			for (Field f : fields.fieldList) {
				fieldList.add(new Field(f));
			}
		}
	}

	public Fields() {
		fieldList = new ArrayList<Field>();
	}

	/** Gets the value of the field property.
	 * 
	 * 
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the
	 * returned list will be present inside the JAXB object. This is why there is no set method for the fieldList property.
	 * 
	 * For example, to add a new item, do as follows:
	 * 
	 * getFieldList().add(newItem);
	 * 
	 * Objects of the following type(s) are allowed in the list {@link Field } */
	public List<Field> getFieldList() {
		if (fieldList == null) {
			fieldList = new ArrayList<Field>();
		}
		return this.fieldList;
	}

}