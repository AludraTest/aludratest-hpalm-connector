package org.aludratest.hpalm.testutil;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.http.HttpException;

public interface XmlEndpoint {

	public void handle(HttpServletRequest request, ResponseXmlBuilder builder) throws IOException, ServletException,
			HttpException;

}
