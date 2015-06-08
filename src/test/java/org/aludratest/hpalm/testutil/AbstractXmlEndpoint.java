package org.aludratest.hpalm.testutil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public abstract class AbstractXmlEndpoint implements XmlEndpoint {

	protected final Document getXml(HttpServletRequest request) throws IOException, JDOMException {
		InputStream in = request.getInputStream();
		if (in == null) {
			return null;
		}

		try {
			InputStreamReader reader = new InputStreamReader(in, "UTF-8");
			return new SAXBuilder().build(reader);
		}
		finally {
			IOUtils.closeQuietly(in);
		}
	}

}
