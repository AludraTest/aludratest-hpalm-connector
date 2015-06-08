package org.aludratest.hpalm.infrastructure;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "QCRestException")
public class QCRestException {

	@XmlElement(name = "Id")
	private String id;

	@XmlElement(name = "Title")
	private String title;

	public QCRestException() {
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

}
