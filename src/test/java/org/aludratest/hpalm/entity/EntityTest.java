package org.aludratest.hpalm.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class EntityTest {

	@Test
	public void testConstructor() {
		Entity e = new Entity();
		assertNull(e.getType());
		assertNull(e.getFields());
		assertEquals(0, e.getId());
	}

	@Test
	public void testCopyConstructor() {
		Entity e1 = new Entity();
		Fields fields = new Fields();
		Field f = new Field();
		f.setName("test");
		f.getValue().add("test1");
		fields.getFieldList().add(f);
		e1.setFields(fields);

		Entity e2 = new Entity(e1);
		assertEquals("test1", e1.getStringFieldValue("test"));
		assertEquals("test1", e2.getStringFieldValue("test"));

		// direct modification of field
		f.getValue().clear();
		f.getValue().add("test2");
		assertEquals("test2", e1.getStringFieldValue("test"));
		assertEquals("test1", e2.getStringFieldValue("test"));
	}

	@Test
	public void testGetStringFieldValue() {
		Entity e1 = new Entity();
		Fields fields = new Fields();
		Field f = new Field();
		f.setName("test");
		f.getValue().add("test1");
		f.getValue().add("test2");
		fields.getFieldList().add(f);
		e1.setFields(fields);

		assertEquals("test1", e1.getStringFieldValue("test"));
	}

	@Test
	public void testGetLongFieldValue() {
		Entity e1 = new Entity();
		Fields fields = new Fields();
		Field f = new Field();
		f.setName("test");
		f.getValue().add("1");
		f.getValue().add("2");
		fields.getFieldList().add(f);
		e1.setFields(fields);

		assertEquals(1, e1.getLongFieldValue("test"));
	}

	@Test
	public void testGetId() {
		Entity e1 = new Entity();
		Fields fields = new Fields();
		Field f = new Field();
		f.setName("id");
		f.getValue().add("23");
		fields.getFieldList().add(f);
		e1.setFields(fields);

		assertEquals(23, e1.getId());
	}

}
