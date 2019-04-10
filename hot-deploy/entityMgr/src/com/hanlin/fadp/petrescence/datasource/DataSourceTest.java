package com.hanlin.fadp.petrescence.datasource;

import static org.junit.Assert.*;

import java.util.Map;

import javolution.util.FastMap;

import org.junit.Before;
import org.junit.Test;

public class DataSourceTest {
	public static void main(String[] args) {
		new DataSourceTest().testAddDataSource();
	}
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAddDataSource() {
		System.setProperty("fadp.home", "A:/");
		Map<String, Object> context=new FastMap<>();
		context.put("field-type-name", "mysql");
		DataSourceWorker.addDataSource(null, context);
		fail("Not yet implemented");
	}

}
