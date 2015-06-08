package org.aludratest.hpalm.testutil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.aludratest.hpalm.infrastructure.HpAlmUtil;
import org.eclipse.jetty.http.HttpException;

public class DefaultTimeEndpoint extends AbstractXmlEndpoint {

	private TimeZone timeZone = TimeZone.getDefault();

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	@Override
	public void handle(HttpServletRequest request, ResponseXmlBuilder builder) throws IOException, ServletException,
			HttpException {
		Date now = new Date();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(timeZone);

		builder.startElement("ServerTime");
		builder.startElement("TimeInMillis").writeText(HpAlmUtil.DF_ID.format(now.getTime())).endElement();
		builder.startElement("DateTime").writeText(sdf.format(now)).endElement();
		builder.endElement();
	}

}
