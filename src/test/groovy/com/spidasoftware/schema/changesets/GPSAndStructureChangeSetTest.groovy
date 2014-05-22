package com.spidasoftware.schema.changesets
import com.spidasoftware.schema.validation.Validator
import net.sf.json.JSONObject
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

public class GPSAndStructureChangeSetTest {

	GPSAndStructureChangeSet changeSet

	@Before
	public void setUp() throws Exception {
		changeSet = new GPSAndStructureChangeSet()
	}

	@Test
	public void testConvertLocationGPS() throws Exception {

		def json = new JSONObject()
		json.put("latitude", 1)
		json.put("longitude", 2)
		changeSet.convertLocationGPS(json)

		assertNull("Should have removed latitude", json.latitude)
		assertNull("Should have removed longitude", json.longitude)


		def report = new Validator().validateAndReport("/v1/schema/general/geometry.schema", json.geographicCoordinate.toString())

		assertEquals("should have copied lat", 1, json.geographicCoordinate.coordinates[1])
		assertEquals("should have copied long", 2, json.geographicCoordinate.coordinates[0])

		assertTrue("Should succeed", report.isSuccess())
	}

	@Test
	public void testConvertStructure() throws Exception {
		def json = new JSONObject()
		json.put("design", new JSONObject())
		json.design.put("pole", new JSONObject())
		changeSet.convertStructure(json)

		assertNull("Should not have a design", json.design)
		assertNotNull("Should have a structure", json.structure)
		assertNotNull("Should have the contents of the old design", json.structure.pole)
	}
}