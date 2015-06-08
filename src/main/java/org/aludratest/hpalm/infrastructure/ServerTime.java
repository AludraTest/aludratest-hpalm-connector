package org.aludratest.hpalm.infrastructure;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ServerTime")
public class ServerTime {

	@XmlElement(name = "TimeInMillis")
	private String timeInMillis;

	@XmlElement(name = "DateTime")
	private String dateTime;

	public String getTimeInMillis() {
		return timeInMillis;
	}

	public String getDateTime() {
		return dateTime;
	}

}
