/*
 * Copyright (C) 2015 Hamburg Sud and the contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aludratest.hpalm.infrastructure;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.aludratest.hpalm.entity.Entity;
import org.aludratest.hpalm.entity.Field;
import org.aludratest.hpalm.entity.TestInstanceBuilder;
import org.aludratest.hpalm.testutil.DefaultHpAlmServlet;
import org.aludratest.hpalm.testutil.DefaultTimeEndpoint;
import org.aludratest.hpalm.testutil.MockingTestServer;
import org.aludratest.hpalm.testutil.ResponseXmlBuilder;
import org.eclipse.jetty.http.HttpException;
import org.junit.Assert;
import org.junit.Test;

public class HpAlmSessionTest {

	@Test
	public void testTimeZoneCalculation() throws Exception {
		MockingTestServer server = new MockingTestServer();

		DefaultHpAlmServlet servlet = new DefaultHpAlmServlet();
		server.setHpAlmServlet(servlet);

		DefaultTimeEndpoint timeEndpoint = new DefaultTimeEndpoint();
		TimeZone tz = TimeZone.getTimeZone("GMT+9:30");
		timeEndpoint.setTimeZone(tz);
		servlet.setTimeEndpoint(timeEndpoint);

		server.startServer();

		String url = server.getBaseUrl();
		HpAlmSession session = HpAlmSession.create(url, "DEFAULT", "Test", "test1", "test1234");

		assertEquals(tz, session.determineServerTimeZone());

		server.stopServer();
	}

	@Test
	public void testQueryCollection() throws Exception {
		MockingTestServer server = new MockingTestServer();

		DefaultHpAlmServlet servlet = new DefaultHpAlmServlet();
		server.setHpAlmServlet(servlet);
		server.startServer();

		// register a test instance
		TestInstanceBuilder builder = new TestInstanceBuilder();
		Entity e = builder.setStatus("Passed").create();
		Field id = new Field();
		id.setName("id");
		id.getValue().add("123");
		e.getFields().getFieldList().add(id);
		servlet.setEntities("test-instance", Collections.singletonList(e));

		String url = server.getBaseUrl();

		HpAlmSession session = HpAlmSession.create(url, "DEFAULT", "Test", "test1", "test1234");

		EntityCollection ec = session.queryEntities("test-instance", "id[>0]");
		assertEquals(1, ec.getTotalCount());

		Entity eReturn = ec.iterator().next();
		assertEquals(e.getLongFieldValue("id"), eReturn.getLongFieldValue("id"));

		session.logout();
		server.stopServer();
	}

	@Test
	public void testConnectTimeout() throws Exception {
		// should throw a Connect Timeout Exception after 5 seconds
		long start = System.currentTimeMillis();
		try {
			// try to connect to a non-routeable address
			HpAlmSession.create("http://10.255.255.1:8080/somePath", "test", "test", "test", "test", 1000, 0);
			Assert.fail("Expected Connection Timeout, but nothing thrown");
		}
		catch (SocketTimeoutException e) {
			// OK, the kind of exception we expected
			long waitTime = System.currentTimeMillis() - start;
			Assert.assertTrue(waitTime >= 1000 && waitTime < 5000);
		}
	}

	// uses connect timeout, but should not fail, because connection should be fast enough
	// (ensures that connection timeout is not used for something else)
	@Test
	public void testPositiveWithConnectTimeout() throws Exception {
		MockingTestServer server = new MockingTestServer();

		DefaultHpAlmServlet servlet = new DefaultHpAlmServlet();
		server.setHpAlmServlet(servlet);

		// use an endpoint which is a little bit slower - must not raise CONNECT timeout
		servlet.setTimeEndpoint(slowTimeEndpoint);

		server.startServer();

		String url = server.getBaseUrl();
		HpAlmSession session = HpAlmSession.create(url, "DEFAULT", "Test", "test1", "test1234", 1000, 0);
		session.determineServerTimeZone();

		session.logout();
		server.stopServer();
	}

	@Test
	public void testRequestTimeout() throws Exception {
		MockingTestServer server = new MockingTestServer();

		DefaultHpAlmServlet servlet = new DefaultHpAlmServlet();
		server.setHpAlmServlet(servlet);

		// use an endpoint which is a little bit slower
		servlet.setTimeEndpoint(slowTimeEndpoint);

		server.startServer();

		String url = server.getBaseUrl();
		HpAlmSession session = HpAlmSession.create(url, "DEFAULT", "Test", "test1", "test1234", 5000, 1000);

		long start = System.currentTimeMillis();
		try {
			session.determineServerTimeZone();
			Assert.fail("Expected request timeout, but passed");
		}
		catch (SocketTimeoutException e) {
			// check waiting time
			long waitTime = System.currentTimeMillis() - start;
			Assert.assertTrue(waitTime >= 1000 && waitTime < 5000);
		}
		session.logout();
		server.stopServer();
	}

	// a little bit slower time endpoint
	private DefaultTimeEndpoint slowTimeEndpoint = new DefaultTimeEndpoint() {
		@Override
		public void handle(HttpServletRequest request, ResponseXmlBuilder builder)
				throws IOException, ServletException, HttpException {
			try {
				Thread.sleep(2000);
			}
			catch (InterruptedException e) {
			}
			super.handle(request, builder);
		}
	};
}
