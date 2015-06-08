package org.aludratest.hpalm.entity;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.aludratest.hpalm.infrastructure.HpAlmUtil;

public abstract class AbstractEntityBuilder {

	protected static final DecimalFormat DF_INTEGER = HpAlmUtil.DF_ID;

	protected static final DateFormat DF_DATE = new SimpleDateFormat("yyyy-MM-dd");

	protected static final DateFormat DF_TIME = new SimpleDateFormat("HH:mm:ss");

	static {
		setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private Entity entity;

	private String entityTypeName;

	public AbstractEntityBuilder(String entityTypeName) {
		this.entityTypeName = entityTypeName;
		create();
	}

	public static void setTimeZone(TimeZone timeZone) {
		DF_DATE.setTimeZone(timeZone);
		DF_TIME.setTimeZone(timeZone);
	}

	public final Entity create() {
		Entity e = entity;
		entity = entity == null ? new Entity() : new Entity(entity);
		entity.setType(entityTypeName);
		if (entity.getFields() == null) {
			entity.setFields(new Fields());
		}
		return e;
	}

	protected final void setValue(String fieldName, String value) {
		List<Field> fieldList = entity.getFields().getFieldList();
		for (Field f : fieldList) {
			if (f.getName().equals(fieldName)) {
				f.getValue().clear();
				f.getValue().add(value);
				return;
			}
		}

		Field f = new Field();
		f.setName(fieldName);
		f.getValue().add(value);
		entity.getFields().getFieldList().add(f);
	}

}
