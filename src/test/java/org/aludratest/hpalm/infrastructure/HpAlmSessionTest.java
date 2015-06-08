package org.aludratest.hpalm.infrastructure;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.TimeZone;

import org.aludratest.hpalm.entity.Entity;
import org.aludratest.hpalm.entity.Field;
import org.aludratest.hpalm.entity.TestInstanceBuilder;
import org.aludratest.hpalm.testutil.DefaultHpAlmServlet;
import org.aludratest.hpalm.testutil.DefaultTimeEndpoint;
import org.aludratest.hpalm.testutil.MockingTestServer;
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

}
