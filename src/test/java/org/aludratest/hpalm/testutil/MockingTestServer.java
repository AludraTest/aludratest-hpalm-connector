package org.aludratest.hpalm.testutil;

import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class MockingTestServer {

	public static final int HTTP_PORT = 11889;

	private Server server;

	private Servlet hpAlmServlet = new DefaultHpAlmServlet();

	public MockingTestServer() {
	}

	public String getBaseUrl() {
		return "http://localhost:" + HTTP_PORT + "/qcbin";
	}

	public void setHpAlmServlet(Servlet hpAlmServlet) {
		this.hpAlmServlet = hpAlmServlet;
	}

	public Servlet getHpAlmServlet() {
		return hpAlmServlet;
	}

	public void startServer() throws Exception {
		server = new Server(HTTP_PORT);

		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		handler.setContextPath("/qcbin");
		ServletHolder holder = new ServletHolder("hpalm-mock", hpAlmServlet);
		handler.addServlet(holder, "/*");

		server.setHandler(handler);
		server.start();
		while (!server.isStarted()) {
			Thread.sleep(100);
		}
	}

	public void stopServer() throws Exception {
		server.stop();
		server.join();
		server = null;
	}

}
